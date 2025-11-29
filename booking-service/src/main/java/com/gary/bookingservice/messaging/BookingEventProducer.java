package com.gary.bookingservice.messaging;

import com.gary.events.BookingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.gary.common.KafkaConfigProperties.MOVIE_BOOKING_EVENTS_TOPIC;

@Component
@Slf4j
public class BookingEventProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public BookingEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBookingEvents(final BookingCreatedEvent bookingCreatedEvent) {
        try {
            log.info("Publishing booking event: {}", bookingCreatedEvent);
            kafkaTemplate.send(MOVIE_BOOKING_EVENTS_TOPIC, bookingCreatedEvent);
        } catch (Exception e) {
            log.error("Error publishing booking event: {}", bookingCreatedEvent, e);
        }
    }
}
