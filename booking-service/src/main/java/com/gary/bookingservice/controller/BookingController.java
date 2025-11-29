package com.gary.bookingservice.controller;

import com.gary.bookingservice.service.BookingService;
import com.gary.request.BookingRequest;
import com.gary.response.BookingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking-service")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(final BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookSeat")
    public ResponseEntity<?> bookSeat(@RequestBody BookingRequest bookingRequest) {
        BookingResponse response = bookingService.bookSeats(bookingRequest);
        return ResponseEntity.ok(response);
    }
}
