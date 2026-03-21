package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.payload.dto.SubscriptionPlanDTO;
import com.example.payload.response.ApiResponse;
import com.example.service.SubscriptionPlanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/subscription-plans")
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;


    @GetMapping
public ResponseEntity<?> getAllSubscriptionPlans() {
    List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllSubscriptionPlan();
    return ResponseEntity.ok(plans);
}

@PostMapping("/admin")
public ResponseEntity<SubscriptionPlanDTO> createSubscriptionPlan(
        @Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO
) throws Exception {

    SubscriptionPlanDTO plans = subscriptionPlanService.createSubscriptionPlan(subscriptionPlanDTO);

return ResponseEntity.status(201).body(plans);
}

@PutMapping("/admin/{id}")
public ResponseEntity<SubscriptionPlanDTO> updateSubscriptionPlan(
        @Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO,
        @PathVariable Long id
) throws Exception {

    SubscriptionPlanDTO plans = subscriptionPlanService.updateSubscriptionPlan(
            id, subscriptionPlanDTO
    );

    return ResponseEntity.ok(plans);
}

@DeleteMapping("/admin/{id}")
public ResponseEntity<?> deleteSubscriptionPlan(
        @PathVariable long id
) throws Exception {

    subscriptionPlanService.deleteSubscriptionPlan(id);

    ApiResponse res = new ApiResponse("plan deleted successfully", true);

    return ResponseEntity.ok(res);
}

}
