package com.gary.events;

public record BookingPaymentEvent(
        String bookingId,
        boolean paymentCompleted,
        long amount
) {}