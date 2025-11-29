package com.gary.seatinventoryservice.messaging;

import com.gary.events.SeatReservedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.gary.common.KafkaConfigProperties.SEAT_RESERVED_TOPIC;

@Component
@Slf4j
public class SeatReserveProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public SeatReserveProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishSeatReserveEvent(final SeatReservedEvent seatReserveEvent) {
        try {
            log.info("SeatReserveProducer :: Publishing seat reserve event: {}", seatReserveEvent);
            kafkaTemplate.send(SEAT_RESERVED_TOPIC, seatReserveEvent);
        } catch (Exception e) {
            log.error("SeatReserveProducer :: Error publishing seat reserve event: {}: {}",
                    seatReserveEvent, e.getMessage());
        }
    }
}
