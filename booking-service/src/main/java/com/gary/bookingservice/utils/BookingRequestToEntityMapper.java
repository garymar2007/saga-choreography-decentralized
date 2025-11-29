package com.gary.bookingservice.utils;

import com.gary.bookingservice.entity.Booking;
import com.gary.request.BookingRequest;

import java.util.UUID;

public class BookingRequestToEntityMapper {
    public static Booking map(BookingRequest request) {
        var reservationCode = UUID.randomUUID().toString().split("-")[0];
        Booking booking = new Booking();
        booking.setId(System.currentTimeMillis());
        booking.setBookingCode(reservationCode);
        booking.setSeatIds(request.seatIds());
        booking.setShowId(request.showId());
        booking.setUserId(request.userId());
        booking.setBookingCode(reservationCode);
        booking.setStatus("CONFIRMED");
        booking.setCreatedAt(java.time.Instant.now());
        booking.setAmount(request.amount());
        return booking;
    }
}
