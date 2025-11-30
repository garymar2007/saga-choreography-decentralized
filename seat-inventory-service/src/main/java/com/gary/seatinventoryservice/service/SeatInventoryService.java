package com.gary.seatinventoryservice.service;

import com.gary.events.BookingCreatedEvent;
import com.gary.events.SeatReservedEvent;
import com.gary.seatinventoryservice.entity.SeatInventory;
import com.gary.seatinventoryservice.messaging.SeatReserveProducer;
import com.gary.seatinventoryservice.repository.SeatInventoryRepository;
import com.gary.seatinventoryservice.utils.SeatStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SeatInventoryService {
    private SeatInventoryRepository seatInventoryRepository;
    private SeatReserveProducer seatReserveProducer;

    public SeatInventoryService(
            final SeatInventoryRepository seatInventoryRepository,
            final SeatReserveProducer seatReserveProducer
    ) {
        this.seatInventoryRepository = seatInventoryRepository;
        this.seatReserveProducer = seatReserveProducer;
    }

    public void handleBooking(BookingCreatedEvent event) {
        log.info("SeatInventoryService ::  Processing bookingCreated for bookingId {}", event.bookingId());
        // Fetch seat inventories for the given show and seat numbers
        List<SeatInventory> seats = seatInventoryRepository
                .findByShowIdAndSeatNumberIn(event.showId(), event.seatIds());

        // Check if all seats are available
        boolean allAvailable = seats.stream()
                .allMatch(s -> s.getStatus() == SeatStatus.AVAILABLE);

        if (allAvailable) {
            // Update seat status to LOCKED and set current booking ID
            seats.forEach(s -> {
                s.setStatus(SeatStatus.LOCKED);
                s.setCurrentBookingId(event.bookingId());
            });
            seatInventoryRepository.saveAll(seats);
            // Publish seat reserved event
            seatReserveProducer
                    .publishSeatReserveEvent(new SeatReservedEvent(event.bookingId(), true, event.amount()));
            log.info("SeatInventoryService:: Seats locked successfully for bookingId {}", event.bookingId());
        }else{
            log.warn("SeatInventoryService:: Seat locking failed for bookingId {}. Some seats are not available.", event.bookingId());
            // Publish seat reserved event with failure
            seatReserveProducer
                    .publishSeatReserveEvent(new SeatReservedEvent(event.bookingId(), false, event.amount()));
        }
    }

    public void releaseSeatsOnPaymentFailure(final String bookingId) {
        log.info("SeatInventoryService :: Release seats on payment failure for bookingId {}", bookingId);
        List<SeatInventory> bookingSeats =  seatInventoryRepository.findByCurrentBookingId(bookingId);
        bookingSeats.forEach(seatInventory -> {
            seatInventory.setStatus(SeatStatus.AVAILABLE);
            seatInventory.setCurrentBookingId(null);
        });
        seatInventoryRepository.saveAll(bookingSeats);
        log.info("SeatInventoryService :: Released seats on payment failure for bookingId {}", bookingId);

        seatReserveProducer.publishSeatReserveEvent(
                new SeatReservedEvent(bookingId, false, 0));
    }
}
