package com.example.payload.response;

import com.example.domain.PaymentGateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateResponse {

    private Long paymentId;

    private PaymentGateway gateway;

    private String razorpayOrderId;

    private String key;        // 🔥 REQUIRED for Razorpay
    private String currency;   // e.g. INR

    private Long amount;

    private String description;

    private String message;

    private Boolean success;
}
