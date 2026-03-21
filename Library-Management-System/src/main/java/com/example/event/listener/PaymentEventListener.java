package com.example.event.listener;

import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.event.PaymentSuccessEvent;
import com.example.exception.SubscriptionException;
import com.example.model.Payment;
import com.example.service.SubscriptionService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final SubscriptionService subscriptionService;

    @Transactional
    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {

        Payment payment = event.getPayment();

        log.info("🔥 Received payment event: {}", payment.getId());

        // Only process successful payments
        if (payment.getStatus() == null || !payment.getStatus().isSuccess()) {
            log.warn("Ignoring non-success payment: {}", payment.getId());
            return;
        }

        if (payment.getSubscription() == null) {
            throw new SubscriptionException("No subscription linked to this payment");
        }

        switch (payment.getPaymentType()) {

            case MEMBERSHIP:
                log.info("👉 Activating subscription for paymentId={}", payment.getId());

                subscriptionService.activateSubscription(
                        payment.getSubscription().getId(),
                        payment.getId()
                );
                break;

            default:
                log.info("No activation required for type: {}", payment.getPaymentType());
        }
    }
}