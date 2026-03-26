package com.example.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.domain.PaymentGateway;
import com.example.domain.PaymentStatus;
import com.example.event.publisher.PaymentEventPublisher;
import com.example.mapper.PaymentMapper;
import com.example.model.Payment;
import com.example.model.Subscription;
import com.example.model.User;
import com.example.payload.dto.PaymentDTO;
import com.example.payload.request.PaymentInitiateRequest;
import com.example.payload.request.PaymentVerifyRequest;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.payload.response.RazorpayOrderResponse;
import com.example.repository.PaymentRepository;
import com.example.repository.SubscriptionRepository;
import com.example.repository.UserRepository;
import com.example.service.PaymentService;
import com.example.service.UserService;
import com.example.service.gateway.RazorpayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{
    
private final UserRepository userRepository;
private final UserService userService;
private final SubscriptionRepository subscriptionRepository;
private final PaymentRepository paymentRepository;
private final RazorpayService razorpayService;
private final PaymentMapper paymentMapper;
private final PaymentEventPublisher paymentEventPublisher;

@Value("${razorpay.key.id}")
private String razorpayKeyId;

@Value("${razorpay.key.secret}")
private String razorpayKeySecret;

@Override
public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {

    // 🔐 Always get logged-in user
    User user = userService.getCurrentUser();

    if (request.getSubscriptionId() == null) {
        throw new RuntimeException("Subscription ID is required");
    }

    // Fetch subscription
    Subscription sub = subscriptionRepository
            .findById(request.getSubscriptionId())
            .orElseThrow(() -> new RuntimeException("Subscription not found"));

    // Create payment
    Payment payment = new Payment();
    payment.setUser(user);
    payment.setSubscription(sub);
    payment.setPaymentType(request.getPaymentType());
    payment.setGateway(request.getGateway());

    // 🔥 Amount from backend (NOT request)
    payment.setAmount(sub.getPrice());

    payment.setDescription("Library Subscription - " + sub.getPlanName());
    payment.setStatus(PaymentStatus.PENDING);
    payment.setTransactionId("TXN-" + UUID.randomUUID());
    payment.setInitiatedAt(LocalDateTime.now());

    payment = paymentRepository.save(payment);

    // 🔥 Razorpay Order creation (NOT PaymentLink)
    RazorpayOrderResponse order = razorpayService.createOrder(payment);

    // Save orderId
    payment.setGatewayOrderId(order.getOrderId());
    paymentRepository.save(payment);

    // Build response
    return PaymentInitiateResponse.builder()
            .paymentId(payment.getId())
            .gateway(payment.getGateway())
            .razorpayOrderId(order.getOrderId())
            .key(razorpayKeyId) // your key
            .currency("INR")
            .amount(payment.getAmount())
            .description(payment.getDescription())
            .success(true)
            .message("Payment initiated successfully")
            .build();
}


@Override
public PaymentDTO verifyPayment(PaymentVerifyRequest req) {

    log.info("🔍 Verifying payment for paymentId={}", req.getPaymentId());

    // 🔐 Fetch payment safely
    Payment payment = paymentRepository.findById(req.getPaymentId())
            .orElseThrow(() -> new RuntimeException("Payment not found"));

    log.info("📦 Payment fetched: id={}, status={}, type={}",
            payment.getId(), payment.getStatus(), payment.getPaymentType());

    if (payment.getGateway() == PaymentGateway.RAZORPAY) {

        // 🔥 Signature verification (TEMPORARY TRUE)
        boolean isValid = razorpayService.verifySignature(
                req.getRazorpayOrderId(),
                req.getRazorpayPaymentId(),
                req.getRazorpaySignature()
        );

        // boolean isValid = true; // 👈 TEMPORARY

        if (!isValid) {
            log.error("❌ Payment verification failed for paymentId={}", payment.getId());

            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Invalid Razorpay signature");
            paymentRepository.save(payment);

            throw new RuntimeException("Payment verification failed");
        }

        // ✅ Success case
        payment.setGatewayPaymentId(req.getRazorpayPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCompletedAt(LocalDateTime.now());

        log.info("✅ VERIFY SUCCESS for paymentId={}", payment.getId());

        payment = paymentRepository.save(payment);

        // 🔥 IMPORTANT DEBUG BEFORE EVENT
        log.info("📤 Publishing event for paymentId={}", payment.getId());
        log.info("🔗 Payment Subscription: {}", payment.getSubscription());

        // 🔥 Trigger event
        paymentEventPublisher.publishPaymentSuccessEvent(payment);
    }

    return paymentMapper.toDTO(payment);
}

@Override
public Page<PaymentDTO> getAllPayments(Pageable pageable) {
    Page<Payment> payments = paymentRepository.findAll(pageable);
    return payments.map(paymentMapper::toDTO);
}

@Override
public Payment getPaymentById(Long paymentId) {

    return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException(
                    "Payment not found: " + paymentId));
}

@Override
public Page<PaymentDTO> getUserPayments(Pageable pageable) {

    User user = userService.getCurrentUser();

    Page<Payment> payments =
            paymentRepository.findByUserId(user.getId(), pageable);

    return payments.map(paymentMapper::toDTO);
}


}
