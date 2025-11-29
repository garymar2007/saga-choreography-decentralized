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

    public void handleBooking(BookingCreatedEvent bookingCreatedEvent) {
        log.info("SeatInventoryService ::  Processing bookingCreated for bookingId {}", bookingCreatedEvent.bookingId());
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
