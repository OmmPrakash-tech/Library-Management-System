package com.example.mapper;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.model.Reservation;
import com.example.payload.dto.ReservationDTO;

@Component
public class ReservationMapper {

    public ReservationDTO toDTO(Reservation reservation) {

        if (reservation == null) {
            return null;
        }

        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());

        // User information
        if (reservation.getUser() != null) {
            dto.setUserId(reservation.getUser().getId());
            dto.setUserName(reservation.getUser().getFullName());
            dto.setUserEmail(reservation.getUser().getEmail());
        }

        // Reservation details
        dto.setStatus(reservation.getStatus());
        dto.setReservedAt(reservation.getReservedAt());
        dto.setAvailableAt(reservation.getAvailableAt());
        dto.setAvailableUntil(reservation.getAvailableUntil());
        dto.setFulfilledAt(reservation.getFulfilledAt());
        dto.setCancelledAt(reservation.getCancelledAt());
        dto.setQueuePosition(reservation.getQueuePosition());
        dto.setNotificationSent(reservation.getNotificationSent());
        dto.setNotes(reservation.getNotes());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());

        // Computed fields
        dto.setExpired(reservation.hasExpired());
        dto.setCanBeCancelled(reservation.canBeCancelled());

        // Calculate hours until expiry
        if (reservation.getAvailableUntil() != null) {

            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(reservation.getAvailableUntil())) {
                long hours = Duration
                        .between(now, reservation.getAvailableUntil())
                        .toHours();

                dto.setHoursUntilExpiry(hours);
            } else {
                dto.setHoursUntilExpiry(0L);
            }
        }

        return dto;
    }
}
