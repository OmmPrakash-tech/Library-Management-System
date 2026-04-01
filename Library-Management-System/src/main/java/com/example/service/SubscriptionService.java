package com.example.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.payload.dto.SubscriptionDTO;
import com.example.payload.response.PaymentInitiateResponse;

public interface SubscriptionService {

    PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO);

    SubscriptionDTO getUsersActiveSubscription();

    List<SubscriptionDTO> getAllUsersSubscriptions();

    List<SubscriptionDTO> getAllActiveSubscriptions();
    
   // SubscriptionDTO getUsersActiveSubscription(Long userId);

    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason);

SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId);

//List<SubscriptionDTO> getUsersActiveSubscriptions();

    Page<SubscriptionDTO> getAllSubscriptions(Pageable pageable);

    void deactivateExpiredSubscriptions();

    List<SubscriptionDTO> assignSubscriptionsToUsers(List<SubscriptionDTO> subscriptions);

}