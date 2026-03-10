package com.example.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.mapper.SubscriptionPlanMapper;
import com.example.model.SubscriptionPlan;
import com.example.model.User;
import com.example.payload.dto.SubscriptionPlanDTO;
import com.example.repository.SubscriptionPlanRepository;
import com.example.service.SubscriptionPlanService;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionPlanMapper planMapper;
    private final UserService userService;

    @Override
    public SubscriptionPlanDTO createSubscriptionPlan(SubscriptionPlanDTO planDTO) {

        if (planRepository.existsByPlanCode(planDTO.getPlanCode())) {
            throw new RuntimeException("Plan code already exists");
        }

        SubscriptionPlan plan = planMapper.toEntity(planDTO);

User currentUser = userService.getCurrentUser();
plan.setCreatedBy(currentUser.getFullName());
plan.setUpdatedBy(currentUser.getFullName());

        SubscriptionPlan savedPlan = planRepository.save(plan);

        return planMapper.toDTO(savedPlan);
    }

    @Override
    public SubscriptionPlanDTO updateSubscriptionPlan(Long planId, SubscriptionPlanDTO planDTO) {

        SubscriptionPlan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        planMapper.updateEntity(existingPlan, planDTO);

        User currentUser = userService.getCurrentUser();
        existingPlan.setUpdatedBy(currentUser.getFullName());

        SubscriptionPlan updatedPlan = planRepository.save(existingPlan);

        return planMapper.toDTO(updatedPlan);
    }

    @Override
    public void deleteSubscriptionPlan(Long planId) {

        SubscriptionPlan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        planRepository.delete(existingPlan);
    }

    @Override
    public List<SubscriptionPlanDTO> getAllSubscriptionPlan() {

        List<SubscriptionPlan> planList = planRepository.findAll();

        return planList.stream()
                .map(planMapper::toDTO)
                .collect(Collectors.toList());
    }
}
