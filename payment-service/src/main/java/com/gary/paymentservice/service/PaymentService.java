package com.gary.paymentservice.service;

import com.gary.events.BookingPaymentEvent;
import com.gary.events.SeatReservedEvent;
import com.gary.paymentservice.exception.PaymentServiceException;
import com.gary.paymentservice.message.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentEventProducer eventProducer;

    public void processPayment(final SeatReservedEvent event) {
        try {
            log.info("Processing payment for bookingId: {}", event.bookingId());

            if(event.amount() > 2000) {
                log.info("Payment amount exceeds limit for bookingI: {}", event.bookingId());
                eventProducer.publishPaymentFailureEvent(
                        new SeatReservedEvent(event.bookingId(), false, event.amount())
                );
            } else {
                kafkaTemplate.send(event.bookingId(),
                        new BookingPaymentEvent(event.bookingId(), true, event.amount()));
                eventProducer.publishPaymentSuccessEvent(event);
                log.info("✅ Payment completed for bookingId: {}", event.bookingId());
            }
        } catch (Exception ex) {
            log.error("❌ Payment failed for bookingId: {}. Reason: {}", event.bookingId(), ex.getMessage());
            throw new PaymentServiceException("Payment processing failed for bookingId: " + event.bookingId());
        }
    }
}
