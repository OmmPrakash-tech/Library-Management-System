package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.SubscriptionPlan;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long>{

    Boolean existsByPlanCode(String planCode);

    SubscriptionPlan findByPlanCode(String planCode);

    
}
