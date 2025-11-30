package com.gary.paymentservice.listener;

import com.gary.events.SeatReservedEvent;
import com.gary.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.gary.common.KafkaConfigProperties.PAYMENT_EVENT_GROUP;
import static com.gary.common.KafkaConfigProperties.SEAT_RESERVED_TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class SeatReserveEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(topics = SEAT_RESERVED_TOPIC, groupId = PAYMENT_EVENT_GROUP)
    public void consume(SeatReservedEvent event) {
        try {
            log.info("SeatReserveEventConsumer :: Consuming seat reserve event: {}", event);
            if (event.reserved()) {
               paymentService.processPayment(event);
            } else {
                log.warn("Payment failed for bookingId: {} - ", event.bookingId());
            }
        } catch (Exception ex) {
            log.error("Error processing seat reserve event: {}", event, ex);
        }
    }
}
