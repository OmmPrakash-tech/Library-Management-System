package com.example.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.model.Payment;
import com.example.payload.dto.PaymentDTO;
import com.example.payload.request.PaymentInitiateRequest;
import com.example.payload.request.PaymentVerifyRequest;
import com.example.payload.response.PaymentInitiateResponse;

public interface PaymentService {

    /**
     * Initiate a payment (create payment + gateway order)
     */
    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req);

    /**
     * Verify payment and update status
     */
    PaymentDTO verifyPayment(PaymentVerifyRequest req);

    /**
     * Get payment by ID
     */
    Payment getPaymentById(Long paymentId);

    /**
     * Get all payments (admin)
     */
    Page<PaymentDTO> getAllPayments(Pageable pageable);

    /**
     * Get payments of logged-in user
     */
    Page<PaymentDTO> getUserPayments(Pageable pageable);
}