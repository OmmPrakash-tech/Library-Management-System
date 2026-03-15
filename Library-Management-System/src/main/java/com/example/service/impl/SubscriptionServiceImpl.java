package com.example.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.domain.PaymentGateway;
import com.example.domain.PaymentType;
import com.example.exception.SubscriptionException;
import com.example.mapper.SubscriptionMapper;
import com.example.model.Subscription;
import com.example.model.SubscriptionPlan;
import com.example.model.User;
import com.example.payload.dto.SubscriptionDTO;
import com.example.payload.request.PaymentInitiateRequest;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.repository.SubscriptionPlanRepository;
import com.example.repository.SubscriptionRepository;
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

    @Override
    public PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) {

        User user = userService.getCurrentUser();

        SubscriptionPlan plan = subscriptionPlanRepository
                .findById(subscriptionDTO.getPlanId())
                .orElseThrow(() -> new SubscriptionException("Plan not found"));

        Optional<Subscription> activeSubscription =
                subscriptionRepository.findActiveSubscriptionByUserId(
                        user.getId(), LocalDate.now());

        if (activeSubscription.isPresent()) {
            throw new SubscriptionException("User already has an active subscription");
        }

        Subscription subscription = subscriptionMapper.toEntity(subscriptionDTO);

        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.initializeFromPlan();
        subscription.setIsActive(false);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest
        .builder()
        .userId(user.getId())
        .subscriptionId(subscription.getId())
        .paymentType(PaymentType.MEMBERSHIP)
        .gateway(PaymentGateway.RAZORPAY)
        .amount(subscription.getPrice())
       
        .description("Library Subscription - " + plan.getName())
        .build();

        return paymentService.initiatePayment(paymentInitiateRequest);
    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long userId) {

        Subscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDate.now())
                .orElseThrow(() -> new SubscriptionException("No active subscription found"));

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException(
                        "Subscription not found with ID: " + subscriptionId));

        if (!subscription.getIsActive()) {
            throw new SubscriptionException("Subscription is already inactive");
        }

        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(reason != null ? reason : "Cancelled by user");

        subscription = subscriptionRepository.save(subscription);

        log.info("Subscription cancelled successfully: {}", subscriptionId);

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO activeSubscription(Long subscriptionId, Long paymentId) {

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException("Subscription not found"));

        if (subscription.getIsActive()) {
            throw new SubscriptionException("Subscription already active");
        }

        subscription.setIsActive(true);

        subscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {

        List<Subscription> subscriptions = subscriptionRepository.findAll(pageable).getContent();

        return subscriptionMapper.toDTOList(subscriptions);
    }

  @Override
public void deactivateExpiredSubscriptions(Long userId) {

    List<Subscription> expiredSubscriptions =
            subscriptionRepository.findExpiredActiveSubscriptions(LocalDate.now());

    for (Subscription subscription : expiredSubscriptions) {
        subscription.setIsActive(false);
    }

    subscriptionRepository.saveAll(expiredSubscriptions);
}
}