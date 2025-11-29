package com.gary.events;

public record SeatReservedEvent(
        String bookingId,
        boolean reserved,
        long amount
) {}