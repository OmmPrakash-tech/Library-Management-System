package com.example.event.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.event.PaymentSuccessEvent;
import com.example.model.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishPaymentSuccessEvent(Payment payment) {
        log.info("Publishing payment success event for paymentId={}", payment.getId());
        applicationEventPublisher.publishEvent(new PaymentSuccessEvent(payment));
    }
}
