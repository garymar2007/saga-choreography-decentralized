package com.gary.bookingservice.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    private Long id;
    private String bookingCode;
    private String showId;
    @ElementCollection
    private List<String> seatIds;
    private String userId;
    private String status;
    private Instant createdAt;
    private long amount;
}
