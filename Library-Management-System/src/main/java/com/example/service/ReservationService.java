package com.example.service;

import com.example.payload.dto.ReservationDTO;
import com.example.payload.request.ReservationRequest;
import com.example.payload.request.ReservationSearchRequest;
import com.example.payload.response.PageResponse;

public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest reservationRequest);

    ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId);

    ReservationDTO cancelReservation(Long reservationId);

    ReservationDTO fulfillReservation(Long reservationId);

    /**
     * Get my reservations (current user) with filters
     * @param searchRequest Search criteria
     * @return Paginated reservations
     */
    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest);

    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest);

}
