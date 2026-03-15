package com.example.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.exception.SubscriptionException;
import com.example.model.Payment;
import com.example.service.SubscriptionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final SubscriptionService subscriptionService;

    @Async
    @EventListener
    @Transactional
    public void handlePaymentSuccess(Payment payment) throws SubscriptionException {

        switch (payment.getPaymentType()) {

            case FINE:
            case LOST_BOOK_PENALTY:
            case DAMAGED_BOOK_PENALTY:
                break;

            case MEMBERSHIP:
                subscriptionService.activeSubscription(
                        payment.getSubscription().getId(),
                        payment.getId()
                );
        }
    }
}