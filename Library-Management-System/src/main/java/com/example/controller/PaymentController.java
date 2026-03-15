package com.example.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.payload.dto.PaymentDTO;
import com.example.payload.request.PaymentVerifyRequest;
import com.example.payload.response.ApiResponse;
import com.example.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

@PostMapping("/verify")
public ResponseEntity<?> verifyPayment(
        @Valid @RequestBody PaymentVerifyRequest request) {
    try {

        PaymentDTO payment = paymentService.verifyPayment(request);

        return ResponseEntity.ok(payment);

    } catch (Exception e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(e.getMessage(), false));
    }
}

@GetMapping
public ResponseEntity<?> getAllPayments(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDir) {

    Sort sort = sortDir.equalsIgnoreCase("DESC")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    Page<PaymentDTO> payments = paymentService.getAllPayments(pageable);

    return ResponseEntity.ok(payments);
}



}
