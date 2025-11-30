package com.gary.paymentservice.message;

import com.gary.events.BookingPaymentEvent;
import com.gary.events.SeatReservedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.gary.common.KafkaConfigProperties.PAYMENT_EVENTS_TOPIC;

@Component
@Slf4j
public class PaymentEventProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentSuccessEvent(final SeatReservedEvent event) {
        log.info("Publishing payment success event: {}", event);
        BookingPaymentEvent bookingPaymentEvent =
                new BookingPaymentEvent(event.bookingId(), true, event.amount());
        kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event.bookingId(), bookingPaymentEvent);
    }

    public void publishPaymentFailureEvent(final SeatReservedEvent event) {
        log.info("Publihing payment failure event: {}", event);
        BookingPaymentEvent bookingPaymentEvent =
                new BookingPaymentEvent(event.bookingId(), false, event.amount());
        kafkaTemplate.send(PAYMENT_EVENTS_TOPIC, event.bookingId(), bookingPaymentEvent);
    }

}
