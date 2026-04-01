package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.FineStatus;
import com.example.domain.FineType;
import com.example.payload.dto.FineDTO;
import com.example.payload.request.CreateFineRequest;
import com.example.payload.request.WaiveFineRequest;
import com.example.payload.response.PageResponse;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.service.FineService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    @PostMapping
    public ResponseEntity<FineDTO> createFine(
            @Valid @RequestBody CreateFineRequest request) {

        return ResponseEntity.ok(fineService.createFine(request));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<PaymentInitiateResponse> payFine(
            @PathVariable Long id) {

        return ResponseEntity.ok(fineService.initiatePayment(id));
    }

    @PostMapping("/{id}/confirm-payment")
    public ResponseEntity<FineDTO> confirmPayment(
            @PathVariable Long id,
            @RequestParam Long amount,
            @RequestParam String transactionId) {

        return ResponseEntity.ok(
                fineService.applyPayment(id, amount, transactionId)
        );
    }

    @PostMapping("/{id}/waive")
    public ResponseEntity<FineDTO> waiveFine(
            @PathVariable Long id,
            @Valid @RequestBody WaiveFineRequest request) {

        return ResponseEntity.ok(fineService.waiveFine(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FineDTO> getFineById(@PathVariable Long id) {

        return ResponseEntity.ok(fineService.getFineById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<FineDTO>> getMyFines(
            @RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) FineType type) {

        return ResponseEntity.ok(fineService.getMyFines(status, type));
    }

    @GetMapping
    public ResponseEntity<PageResponse<FineDTO>> getAllFines(
            @RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) FineType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                fineService.getAllFines(status, type, userId, page, size)
        );
    }
}