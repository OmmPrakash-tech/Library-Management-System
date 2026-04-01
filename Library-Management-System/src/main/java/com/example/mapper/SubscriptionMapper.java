package com.example.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.exception.SubscriptionException;
import com.example.model.Subscription;
import com.example.model.SubscriptionPlan;
import com.example.model.User;
import com.example.payload.dto.SubscriptionDTO;
import com.example.repository.SubscriptionPlanRepository;
import com.example.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SubscriptionMapper {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;

    /**
     * Convert Subscription entity to DTO
     */


public SubscriptionDTO toDTO(Subscription subscription) {

    if (subscription == null) return null;

    SubscriptionDTO dto = new SubscriptionDTO();

    dto.setId(subscription.getId());

    // user
    if (subscription.getUser() != null) {
        dto.setUserId(subscription.getUser().getId());
        dto.setUserName(subscription.getUser().getFullName());
        dto.setUserEmail(subscription.getUser().getEmail());
    }

    // plan
    if (subscription.getPlan() != null) {
        dto.setPlanId(subscription.getPlan().getId());
    }

    dto.setPlanName(subscription.getPlanName());
    dto.setPlanCode(subscription.getPlanCode());
    dto.setPrice(subscription.getPrice());

    dto.setStartDate(subscription.getStartDate());
    dto.setEndDate(subscription.getEndDate());

    // 🔥 FIX: OVERRIDE ACTIVE LOGIC
    boolean isActive = subscription.getEndDate() != null &&
                       !subscription.getEndDate().isBefore(LocalDate.now());

    dto.setIsActive(
    subscription.getIsActive() && !subscription.isExpired()
);

    dto.setMaxBooksAllowed(subscription.getMaxBooksAllowed());
    dto.setMaxDaysPerBook(subscription.getMaxDaysPerBook());

    dto.setAutoRenew(subscription.getAutoRenew());

    dto.setCancelledAt(subscription.getCancelledAt());
    dto.setCancellationReason(subscription.getCancellationReason());

    dto.setNotes(subscription.getNotes());

    dto.setCreatedAt(subscription.getCreatedAt());
    dto.setUpdatedAt(subscription.getUpdatedAt());

    // calculated
    dto.setDaysRemaining(subscription.getDaysRemaining());
    dto.setIsValid(subscription.isValid());
    dto.setIsExpired(subscription.isExpired());

    return dto;
}

    /**
     * Convert DTO to Subscription entity
     */
public Subscription toEntity(SubscriptionDTO dto) throws SubscriptionException {

    if (dto == null) {
        return null;
    }

    Subscription subscription = new Subscription();

    // Fetch user
    User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new SubscriptionException(
                    "User not found with ID: " + dto.getUserId()));
    subscription.setUser(user);

    // Fetch plan
    SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
            .orElseThrow(() -> new SubscriptionException(
                    "Subscription plan not found with ID: " + dto.getPlanId()));
    subscription.setPlan(plan);

    // 🔥 CORE LOGIC
    subscription.initializeFromPlan();

    // Security-safe defaults
    subscription.setIsActive(false); // activate after payment
    subscription.setAutoRenew(dto.getAutoRenew() != null ? dto.getAutoRenew() : false);

    subscription.setNotes(dto.getNotes());

    return subscription;
}

    /**
     * Convert list of subscriptions to DTOs
     */
    public List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions) {
        if (subscriptions == null) {
            return null;
        }

        return subscriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}