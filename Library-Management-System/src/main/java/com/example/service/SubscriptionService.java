package com.example.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.payload.dto.SubscriptionDTO;
import com.example.payload.response.PaymentInitiateResponse;

public interface SubscriptionService {

    PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getUsersActiveSubscription(Long userId);

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason);

    SubscriptionDTO activeSubscription(Long subscriptionId, Long paymentId);

    List<SubscriptionDTO> getAllSubscriptions(Pageable pageable);

    void deactivateExpiredSubscriptions(Long userId);

}