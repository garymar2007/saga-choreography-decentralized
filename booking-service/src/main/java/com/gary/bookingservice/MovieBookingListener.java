package com.gary.bookingservice;

import com.gary.bookingservice.service.BookingService;
import com.gary.events.SeatReservedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.gary.common.KafkaConfigProperties.MOVIE_BOOKING_GROUP;
import static com.gary.common.KafkaConfigProperties.SEAT_RESERVED_TOPIC;

@Component
@Slf4j
public class MovieBookingListener {
    private BookingService bookingService;
    public MovieBookingListener(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = SEAT_RESERVED_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    public void consumeSeatReservedEvents(SeatReservedEvent event) {
        log.info("MovieBookingListener :: Consuming setReserved event: {}", event);

        if (event.reserved()) {
            log.info("Booking process completed for bookingId {}", event.bookingId());
        } else {
            log.info("Seat reservation failed for bookingId {}", event.bookingId());
            bookingService.handleBookingOnSeatReservationFailure(event.bookingId());
        }
    }
}
