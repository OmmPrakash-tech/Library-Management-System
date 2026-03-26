package com.example.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.domain.PaymentGateway;
import com.example.domain.PaymentStatus;
import com.example.domain.PaymentType;
import com.example.exception.SubscriptionException;
import com.example.mapper.SubscriptionMapper;
import com.example.model.Payment;
import com.example.model.Subscription;
import com.example.model.SubscriptionPlan;
import com.example.model.User;
import com.example.payload.dto.SubscriptionDTO;
import com.example.payload.request.PaymentInitiateRequest;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.repository.SubscriptionPlanRepository;
import com.example.repository.SubscriptionRepository;
import com.example.repository.UserRepository;
import com.example.service.PaymentService;
import com.example.service.SubscriptionService;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    // ================= SUBSCRIBE =================
@Override
public PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) {

    User user = userService.getCurrentUser();

    SubscriptionPlan plan = subscriptionPlanRepository
            .findById(subscriptionDTO.getPlanId())
            .orElseThrow(() -> new SubscriptionException("Plan not found"));

    // Check existing active subscription
    Optional<Subscription> activeSubscription =
            subscriptionRepository.findActiveSubscriptionByUserId(
                    user.getId(), LocalDate.now());

    if (activeSubscription.isPresent()) {
        throw new SubscriptionException("User already has an active subscription");
    }

    // ✅ Create subscription safely (DO NOT trust DTO fully)
    Subscription subscription = new Subscription();
    subscription.setUser(user);
    subscription.setPlan(plan);
    subscription.setAutoRenew(
            subscriptionDTO.getAutoRenew() != null ? subscriptionDTO.getAutoRenew() : false
    );
    subscription.setNotes(subscriptionDTO.getNotes());

    // 🔥 CORE LOGIC
    subscription.initializeFromPlan();

    // Always inactive until payment success
    subscription.setIsActive(false);

    Subscription savedSubscription = subscriptionRepository.save(subscription);

    // Safety check
    if (savedSubscription.getPrice() == null) {
        throw new SubscriptionException("Subscription price not initialized properly");
    }

    // Initiate payment
  PaymentInitiateRequest paymentRequest = PaymentInitiateRequest.builder()
        .subscriptionId(savedSubscription.getId())
        .paymentType(PaymentType.MEMBERSHIP)
        .gateway(PaymentGateway.RAZORPAY)
        .description("Library Subscription - " + plan.getName())
        .build();

    return paymentService.initiatePayment(paymentRequest);
}

    // ================= ACTIVATE AFTER PAYMENT =================
@Override
public SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) {

    Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionException("Subscription not found"));

    if (subscription.getIsActive()) {
        throw new SubscriptionException("Subscription already active");
    }

    // 🔥 Verify payment
    Payment payment = paymentService.getPaymentById(paymentId);

    if (payment == null || payment.getStatus() != PaymentStatus.SUCCESS) {
        throw new SubscriptionException("Payment not successful");
    }

    if (payment.getSubscription() == null) {
        throw new SubscriptionException("Payment not linked to subscription");
    }

    if (!payment.getSubscription().getId().equals(subscriptionId)) {
        throw new SubscriptionException("Invalid payment for this subscription");
    }

    // 🔥 Activate
    subscription.setIsActive(true);
    subscription.setStartDate(LocalDate.now());
    subscription.calculateEndDate();

    subscription = subscriptionRepository.save(subscription);

log.info("🚀 ACTIVATING subscriptionId={}", subscriptionId);

    return subscriptionMapper.toDTO(subscription);
}

    // ================= GET ACTIVE =================
@Override
public SubscriptionDTO getUsersActiveSubscription() {

    User user = userService.getCurrentUser();

    return subscriptionRepository
            .findActiveSubscriptionByUserId(user.getId(), LocalDate.now())
            .map(subscriptionMapper::toDTO)
            .orElse(null); // ✅ no exception
}

    // ================= CANCEL =================
@Override
public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) {

    Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new SubscriptionException(
                    "Subscription not found with ID: " + subscriptionId));

    // 🔒 Security check
    User currentUser = userService.getCurrentUser();
    if (!subscription.getUser().getId().equals(currentUser.getId())) {
        throw new SubscriptionException("You are not authorized to cancel this subscription");
    }

    if (!subscription.getIsActive()) {
        throw new SubscriptionException("Subscription is already inactive");
    }

    // Handle expired case
    if (subscription.isExpired()) {
        subscription.setIsActive(false);
        subscriptionRepository.save(subscription);
        throw new SubscriptionException("Subscription already expired");
    }

    String finalReason = (reason != null && !reason.trim().isEmpty())
            ? reason.trim()
            : "Cancelled by user";

    subscription.setIsActive(false);
    subscription.setCancelledAt(LocalDateTime.now());
    subscription.setCancellationReason(finalReason);

    subscription = subscriptionRepository.save(subscription);

    log.info("Subscription CANCELLED: {}", subscriptionId);

    return subscriptionMapper.toDTO(subscription);
}

    // ================= EXPIRE HANDLER =================
@Override
public void deactivateExpiredSubscriptions() {

    List<Subscription> expiredSubscriptions =
            subscriptionRepository.findExpiredActiveSubscriptions(LocalDate.now());

    for (Subscription subscription : expiredSubscriptions) {
        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason("Auto-expired");
    }

    subscriptionRepository.saveAll(expiredSubscriptions);

    log.info("Deactivated {} expired subscriptions", expiredSubscriptions.size());
}

    // ================= GET ALL =================
@Override
public Page<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {

    Page<Subscription> subscriptions = subscriptionRepository.findAll(pageable);

    return subscriptions.map(subscriptionMapper::toDTO);
}

    // ================= BULK ASSIGN =================
@Override
public List<SubscriptionDTO> assignSubscriptionsToUsers(List<SubscriptionDTO> dtos) {

    List<SubscriptionDTO> result = new ArrayList<>();

    for (SubscriptionDTO dto : dtos) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new SubscriptionException("User not found"));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new SubscriptionException("Plan not found"));

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.initializeFromPlan();
        subscription.setIsActive(true); // direct activation

        Subscription saved = subscriptionRepository.save(subscription);

        result.add(subscriptionMapper.toDTO(saved));
    }

    return result;
}

}