package com.gary.bookingservice.service;

import com.gary.bookingservice.entity.Booking;
import com.gary.bookingservice.messaging.BookingEventProducer;
import com.gary.bookingservice.repository.BookingRepository;
import com.gary.bookingservice.utils.BookingRequestToEntityMapper;
import com.gary.bookingservice.utils.EntityToBookingResponseMapper;
import com.gary.events.BookingCreatedEvent;
import com.gary.request.BookingRequest;
import com.gary.response.BookingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingService {
    private BookingRepository bookingRepository;
    private BookingEventProducer bookingEventProducer;

    public BookingService(
            final BookingRepository bookingRespository,
            final BookingEventProducer bookingEventProducer) {
        this.bookingRepository = bookingRespository;
        this.bookingEventProducer = bookingEventProducer;
    }

    public BookingResponse bookSeats(BookingRequest request) {
        log.info("Booking seats for bookingId: {}", request.reservationId());
        var reservationEntity = BookingRequestToEntityMapper.map(request);
        var savedReservation = bookingRepository.save(reservationEntity);
        var bookingCreatedEvent = buildBookingCreateEvents(savedReservation);
        bookingEventProducer.publishBookingEvents(bookingCreatedEvent);

        var response = EntityToBookingResponseMapper.map(savedReservation);
        log.info("Booking seats for bookingId: {} completed successfully", request.reservationId());

        return response;

    }

    private BookingCreatedEvent buildBookingCreateEvents(Booking savedReservation) {
        return new BookingCreatedEvent(savedReservation.getBookingCode(), savedReservation.getUserId(), savedReservation.getShowId(), savedReservation.getSeatIds(), savedReservation.getAmount());
    }

    public void handleBookingOnSeatReservationFailure(String bookingId) {
        log.info("BookingService :: Handling failure for bookingId: {}", bookingId);
        var bookingDetails = bookingRepository.findByBookingCode(bookingId);
        if (bookingDetails != null) {
            bookingDetails.setStatus("FAILED");
            bookingRepository.save(bookingDetails);
            log.info("BookingService :: Handling failure for bookingId: {} completed successfully", bookingId);
        } else {
            log.warn("BookingService :: Handling failure for bookingId: {} failed as booking not found", bookingId);
        }

    }
}
