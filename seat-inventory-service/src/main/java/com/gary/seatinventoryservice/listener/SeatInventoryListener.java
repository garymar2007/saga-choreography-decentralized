package com.gary.seatinventoryservice.listener;

import com.gary.events.BookingCreatedEvent;
import com.gary.seatinventoryservice.service.SeatInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.gary.common.KafkaConfigProperties.MOVIE_BOOKING_EVENTS_TOPIC;
import static com.gary.common.KafkaConfigProperties.SEAT_EVENT_GROUP;

@Component
@Slf4j
public class SeatInventoryListener {
    private SeatInventoryService seatInventoryService;

    public SeatInventoryListener(SeatInventoryService seatInventoryService) {
        this.seatInventoryService = seatInventoryService;
    }

    @KafkaListener(topics = MOVIE_BOOKING_EVENTS_TOPIC, groupId = SEAT_EVENT_GROUP)
    public void consumeSeatInventoryEvents(final BookingCreatedEvent event) {
        log.info("SeatInventoryListener :: Consuming seat inventory event: {}", event);
        seatInventoryService.handleBooking(event);
    }
}
