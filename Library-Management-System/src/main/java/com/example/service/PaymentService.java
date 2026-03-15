package com.example.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.payload.dto.PaymentDTO;
import com.example.payload.request.PaymentInitiateRequest;
import com.example.payload.request.PaymentVerifyRequest;
import com.example.payload.response.PaymentInitiateResponse;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req);

PaymentDTO verifyPayment(PaymentVerifyRequest req);

Page<PaymentDTO> getAllPayments(Pageable pageable);

}
