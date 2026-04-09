package com.example.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mapper.SubscriptionMapper;
import com.example.model.Subscription;
import com.example.model.User;
import com.example.payload.dto.SubscriptionDTO;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.repository.SubscriptionRepository;
import com.example.service.SubscriptionService;
import com.example.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="https://library-management-system-3d9t.onrender.com")
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    

@GetMapping("/all")
public ResponseEntity<List<SubscriptionDTO>> getAllUsersSubscriptions() {

    return ResponseEntity.ok(
            subscriptionService.getAllUsersSubscriptions()
    );
}

@PostMapping("/subscribe")
public ResponseEntity<PaymentInitiateResponse> subscribe(
        @Valid @RequestBody SubscriptionDTO subscription) {

    return ResponseEntity.ok(subscriptionService.subscribe(subscription));
}

@GetMapping("/admin")
public ResponseEntity<Page<SubscriptionDTO>> getAllSubscriptions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);

    return ResponseEntity.ok(subscriptionService.getAllSubscriptions(pageable));
}



@PostMapping("/admin/deactivate-expired")
public ResponseEntity<ApiResponse> deactivateExpiredSubscriptions() {

    subscriptionService.deactivateExpiredSubscriptions();

    return ResponseEntity.ok(new ApiResponse("Task done!", true));
}

@GetMapping("/active")
public ResponseEntity<?> getAllActiveSubscriptions() {

    List<SubscriptionDTO> subscriptions =
            subscriptionService.getAllActiveSubscriptions();

    if (subscriptions.isEmpty()) {
        return ResponseEntity.ok(
                new ApiResponse("No active subscriptions found", false)
        );
    }

    return ResponseEntity.ok(subscriptions);
}

@PatchMapping("/{subscriptionId}/cancel")
public ResponseEntity<SubscriptionDTO> cancelSubscription(
        @PathVariable Long subscriptionId,
        @RequestParam(required = false) String reason) {

    return ResponseEntity.ok(
            subscriptionService.cancelSubscription(subscriptionId, reason));
}

@PostMapping("/activate")
public ResponseEntity<SubscriptionDTO> activateSubscription(
        @RequestParam Long subscriptionId,
        @RequestParam Long paymentId) {

    return ResponseEntity.ok(
            subscriptionService.activateSubscription(subscriptionId, paymentId));
}


@PostMapping("/bulk-assign")
public ResponseEntity<?> assignSubscriptionsToUsers(
        @RequestBody List<SubscriptionDTO> subscriptions) {

    List<SubscriptionDTO> result =
            subscriptionService.assignSubscriptionsToUsers(subscriptions);

    return ResponseEntity.ok(result);
}

@GetMapping("/user/active")
public ResponseEntity<?> getActiveSubscription() {

    User user = userService.getCurrentUser();

    Optional<Subscription> sub =
            subscriptionRepository.findActiveSubscriptionByUserId(
                    user.getId(),
                    LocalDate.now()
            );

    if (sub.isEmpty()) {
        return ResponseEntity.ok(null);
    }

    return ResponseEntity.ok(subscriptionMapper.toDTO(sub.get()));
}

@PostMapping("/subscribenew")
public ResponseEntity<SubscriptionDTO> subscribeUser(
        @RequestBody Map<String, Long> request) {

    Long userId = request.get("userId");
    Long planId = request.get("planId");

    return ResponseEntity.ok(
            subscriptionService.subscribeUser(userId, planId));
}
}
