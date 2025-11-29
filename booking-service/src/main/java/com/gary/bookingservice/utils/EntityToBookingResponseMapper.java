package com.gary.bookingservice.utils;

import com.gary.bookingservice.entity.Booking;
import com.gary.response.BookingResponse;

public class EntityToBookingResponseMapper {
    public static BookingResponse map(Booking booking) {
        return new BookingResponse(booking.getBookingCode(), booking.getStatus());
    }
}
