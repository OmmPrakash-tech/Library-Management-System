package com.example.payload.request;

import com.example.domain.PaymentGateway;
import com.example.domain.PaymentType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateRequest {

    private Long bookLoanId;

    @NotNull
    private PaymentType paymentType;

    @NotNull
    private PaymentGateway gateway;

    private String description;

    private Long fineId;

    private Long amount;

    private Long subscriptionId;

    private String successUrl;
    private String cancelUrl;
}
