package com.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.domain.BookLoanStatus;
import com.example.domain.ReservationStatus;
import com.example.domain.UserRole;
import com.example.mapper.ReservationMapper;
import com.example.model.Book;
import com.example.model.Reservation;
import com.example.model.User;
import com.example.payload.dto.ReservationDTO;
import com.example.payload.request.CheckoutRequest;
import com.example.payload.request.ReservationRequest;
import com.example.payload.request.ReservationSearchRequest;
import com.example.payload.response.PageResponse;
import com.example.repository.BookLoanRepository;
import com.example.repository.BookRepository;
import com.example.repository.ReservationRepository;
import com.example.service.BookLoanService;
import com.example.service.ReservationService;
import com.example.service.UserService;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final BookLoanService bookLoanService;

    private static final int MAX_RESERVATIONS = 5;

    public ReservationServiceImpl(
            BookLoanRepository bookLoanRepository,
            UserService userService,
            BookRepository bookRepository,
            ReservationRepository reservationRepository,
            ReservationMapper reservationMapper,
            BookLoanService bookLoanService
    ) {
        this.bookLoanRepository = bookLoanRepository;
        this.userService = userService;
        this.bookRepository = bookRepository;
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.bookLoanService = bookLoanService;
    }

    @Override
    public ReservationDTO createReservation(ReservationRequest reservationRequest) {

        User user = userService.getCurrentUser();
        return createReservationForUser(reservationRequest, user.getId());
    }

    @Override
    public ReservationDTO createReservationForUser(ReservationRequest reservationRequest, Long userId) {

        boolean alreadyHasLoan = bookLoanRepository.existsByUserIdAndBookIdAndStatus(
                userId,
                reservationRequest.getBookId(),
                BookLoanStatus.CHECKED_OUT
        );

        if (alreadyHasLoan) {
            throw new RuntimeException("You already have loan on this book");
        }

        User user = userService.getCurrentUser();

        Book book = bookRepository.findById(reservationRequest.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (reservationRepository.hasActiveReservation(userId, book.getId())) {
            throw new RuntimeException("You already have reservation on this book");
        }

        if (book.getAvailableCopies() > 0) {
            throw new RuntimeException("Book is already available");
        }

        long activeReservations = reservationRepository.countActiveReservationsByUser(userId);

        if (activeReservations >= MAX_RESERVATIONS) {
            throw new RuntimeException("You have reserved " + MAX_RESERVATIONS + " times");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setNotificationSent(false);
        reservation.setNotes(reservationRequest.getNotes());

        long pendingCount = reservationRepository.countPendingReservationsByBook(book.getId());
        reservation.setQueuePosition((int) pendingCount + 1);

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        User currentUser = userService.getCurrentUser();

        if (!reservation.getUser().getId().equals(currentUser.getId())
                && currentUser.getRole() != UserRole.ROLE_ADMIN) {

            throw new RuntimeException("You can only cancel your own reservations");
        }

        if (!reservation.canBeCancelled()) {
            throw new RuntimeException(
                    "Reservation cannot be cancelled (current status: " + reservation.getStatus() + ")"
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO fulfillReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        if (reservation.getBook().getAvailableCopies() <= 0) {
            throw new RuntimeException(
                    "Reservation is not available for pickup (current status: " + reservation.getStatus() + ")"
            );
        }

        reservation.setStatus(ReservationStatus.FULFILLED);
        reservation.setFulfilledAt(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        CheckoutRequest request = new CheckoutRequest();
        request.setBookId(reservation.getBook().getId());
        request.setNotes("Assign Booked by Admin");

        bookLoanService.checkoutBookForUser(reservation.getUser().getId(), request);

        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest searchRequest) {

        User user = userService.getCurrentUser();
        searchRequest.setUserId(user.getId());

        return searchReservations(searchRequest);
    }

    @Override
    public PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest searchRequest) {

        Pageable pageable = createPageable(searchRequest);

        Page<Reservation> reservationPage =
                reservationRepository.searchReservationsWithFilters(
                        searchRequest.getUserId(),
                        searchRequest.getBookId(),
                        searchRequest.getStatus(),
                        searchRequest.getActiveOnly() != null
                                ? searchRequest.getActiveOnly()
                                : false,
                        pageable
                );

        return buildPageResponse(reservationPage);
    }

    private PageResponse<ReservationDTO> buildPageResponse(Page<Reservation> reservationPage) {

        List<ReservationDTO> dtos = reservationPage.getContent()
                .stream()
                .map(reservationMapper::toDTO)
                .toList();

        PageResponse<ReservationDTO> response = new PageResponse<>();

        response.setContent(dtos);
        response.setPageNumber(reservationPage.getNumber());
        response.setPageSize(reservationPage.getSize());
        response.setTotalElements(reservationPage.getTotalElements());
        response.setTotalPages(reservationPage.getTotalPages());
        response.setLast(reservationPage.isLast());

        return response;
    }

    private Pageable createPageable(ReservationSearchRequest searchRequest) {

        Sort sort = "ASC".equalsIgnoreCase(searchRequest.getSortDirection())
                ? Sort.by(searchRequest.getSortBy()).ascending()
                : Sort.by(searchRequest.getSortBy()).descending();

        return PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                sort
        );
    }


}