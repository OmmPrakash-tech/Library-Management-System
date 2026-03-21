package com.example.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerifyRequest {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    // Razorpay fields
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;

    // Stripe fields
    private String stripePaymentIntentId;
    private String stripePaymentIntentStatus;
}
