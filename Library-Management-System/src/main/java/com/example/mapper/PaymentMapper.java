package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.model.Payment;
import com.example.payload.dto.PaymentDTO;

 // // Book loan information
    // if (payment.getBookLoan() != null) {
    //     dto.setBookLoanId(payment.getBookLoan().getId());
    // }

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment payment) {

    if (payment == null) {
        return null;
    }

    PaymentDTO dto = new PaymentDTO();
    dto.setId(payment.getId());

    // User
    if (payment.getUser() != null) {
        dto.setUserId(payment.getUser().getId());
        dto.setUserName(payment.getUser().getFullName());
        dto.setUserEmail(payment.getUser().getEmail());
    }

    // Subscription
    if (payment.getSubscription() != null) {
        dto.setSubscriptionId(payment.getSubscription().getId());
    }

    dto.setPaymentType(payment.getPaymentType());
    dto.setStatus(payment.getStatus());
    dto.setGateway(payment.getGateway());
    dto.setAmount(payment.getAmount());

    dto.setTransactionId(payment.getTransactionId());
    dto.setGatewayPaymentId(payment.getGatewayPaymentId());
    dto.setGatewayOrderId(payment.getGatewayOrderId());

    dto.setDescription(payment.getDescription());

    if (payment.getStatus() != null && payment.getStatus().isFailure()) {
        dto.setFailureReason(payment.getFailureReason());
    }

    dto.setInitiatedAt(payment.getInitiatedAt());
    dto.setCompletedAt(payment.getCompletedAt());

    dto.setCreatedAt(payment.getCreatedAt());
    dto.setUpdatedAt(payment.getUpdatedAt());

    return dto;
}

}
