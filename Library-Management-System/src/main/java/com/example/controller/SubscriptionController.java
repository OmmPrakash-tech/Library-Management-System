package com.example.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exception.SubscriptionException;
import com.example.payload.dto.SubscriptionDTO;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.service.SubscriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

@PostMapping("/subscribe")
public ResponseEntity<?> subscribe(
        @Valid @RequestBody SubscriptionDTO subscription) {

    PaymentInitiateResponse dto = subscriptionService.subscribe(subscription);

    return ResponseEntity.ok(dto);
}

@GetMapping("/admin")
public ResponseEntity<?> getAllSubscriptions() {

    int page = 0;
    int size = 10;

    Pageable pageable = PageRequest.of(page, size);

    List<SubscriptionDTO> dtoList =
            subscriptionService.getAllSubscriptions(pageable);

    return ResponseEntity.ok(dtoList);
}

@GetMapping("/admin/deactivate-expired")
public ResponseEntity<?> deactivateExpiredSubscriptions() throws Exception {

    subscriptionService.deactivateExpiredSubscriptions(null);

    ApiResponse res = new ApiResponse("Task done!", true);

    return ResponseEntity.ok(res);
}

@GetMapping("/user/active")
public ResponseEntity<?> getUsersActiveSubscription(
        @RequestParam(required = false) Long userId) throws Exception {

    SubscriptionDTO dto =
            subscriptionService.getUsersActiveSubscription(userId);

    return ResponseEntity.ok(dto);
}

@PostMapping("/cancel/{subscriptionId}")
public ResponseEntity<?> cancelSubscription(

        @PathVariable Long subscriptionId,
        @RequestParam(required = false) String reason) throws SubscriptionException {

    SubscriptionDTO subscription =
            subscriptionService.cancelSubscription(subscriptionId, reason);

    return ResponseEntity.ok(subscription);
}

@PostMapping("/activate")
public ResponseEntity<?> activateSubscription(

        @RequestParam Long subscriptionId,
        @RequestParam Long paymentId) throws SubscriptionException {

    SubscriptionDTO subscription =
            subscriptionService.activeSubscription(subscriptionId, paymentId);

    return ResponseEntity.ok(subscription);
}

}
