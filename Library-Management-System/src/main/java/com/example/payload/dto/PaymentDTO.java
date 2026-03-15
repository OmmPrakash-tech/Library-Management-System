package com.example.payload.dto;

import java.time.LocalDateTime;

import com.example.domain.PaymentGateway;
import com.example.domain.PaymentStatus;
import com.example.domain.PaymentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private Long id;

@NotNull(message = "User ID is mandatory")
private Long userId;

private String userName;

private String userEmail;

private Long bookLoanId;

private Long subscriptionId;

@NotNull(message = "Payment type is mandatory")
private PaymentType paymentType;

private PaymentStatus status;

@NotNull(message = "Payment gateway is mandatory")
private PaymentGateway gateway;

@NotNull(message = "Amount is mandatory")
@Positive(message = "Amount must be positive")
private Long amount;

private String transactionId;

private String gatewayPaymentId;

private String gatewayOrderId;

private String gatewaySignature;



private String description;

private String failureReason;

private Integer retryCount;

private LocalDateTime initiatedAt;

private LocalDateTime completedAt;





private LocalDateTime createdAt;

private LocalDateTime updatedAt;

}
