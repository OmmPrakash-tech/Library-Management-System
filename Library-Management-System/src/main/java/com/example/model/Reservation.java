package com.example.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.domain.ReservationStatus;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who reserved the book
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Book being reserved
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    // When reservation was created
    private LocalDateTime reservedAt;

    // When book becomes available for this reservation
    private LocalDateTime availableAt;

    // Deadline to pick up the book
    private LocalDateTime availableUntil;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    // When reservation cancelled or expired
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Queue position for this book
    @Column(name = "queue_position")
    private Integer queuePosition;

    // Notification flag
    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;

    // Additional notes
    @Column(columnDefinition = "TEXT")
    private String notes;

    // Created timestamp
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Last update timestamp
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    /**
     * Check if reservation can be cancelled
     */
    public boolean canBeCancelled() {
        return status == ReservationStatus.PENDING
                || status == ReservationStatus.AVAILABLE;
    }

    /**
     * Check if reservation expired
     */
    public boolean hasExpired() {
        return status == ReservationStatus.AVAILABLE
                && availableUntil != null
                && LocalDateTime.now().isAfter(availableUntil);
    }

}