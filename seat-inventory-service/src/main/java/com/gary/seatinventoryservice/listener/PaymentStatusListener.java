package com.gary.seatinventoryservice.listener;

import com.gary.events.BookingPaymentEvent;
import com.gary.seatinventoryservice.service.SeatInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.gary.common.KafkaConfigProperties.PAYMENT_EVENTS_TOPIC;
import static com.gary.common.KafkaConfigProperties.SEAT_EVENT_GROUP;

@Component
@Slf4j
public class PaymentStatusListener {
    private SeatInventoryService seatInventoryService;

    public PaymentStatusListener(final SeatInventoryService seatInventoryService) {
        this.seatInventoryService = seatInventoryService;
    }

    @KafkaListener(topics = PAYMENT_EVENTS_TOPIC, groupId = SEAT_EVENT_GROUP)
    public void consumePaymentStatusEvents(BookingPaymentEvent event) {
        log.info("PaymentStatusListener :: Consuming payment status event: {}", event);

        if (event.paymentCompleted()) {
            log.info("Payment completed for bookingId: {}", event.bookingId());
        } else {
            log.warn("Payment failed for bookingId: {}", event.bookingId());
            seatInventoryService.releaseSeatsOnPaymentFailure(event.bookingId());
        }
    }

}
