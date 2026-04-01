package com.example.event;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    // runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void deactivateExpiredSubscriptionsJob() {
        subscriptionService.deactivateExpiredSubscriptions();
    }
}
