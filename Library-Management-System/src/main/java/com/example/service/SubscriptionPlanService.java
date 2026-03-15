package com.example.service;

import java.util.List;

import com.example.model.SubscriptionPlan;
import com.example.payload.dto.SubscriptionPlanDTO;

public interface SubscriptionPlanService {


    SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO);

    SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO);

void deleteSubscriptionPlan(Long planId);

List<SubscriptionPlanDTO> getAllSubscriptionPlan();

SubscriptionPlan getBySubscriptionPlanCode(String subscriptionPlanCode) throws Exception;

}
