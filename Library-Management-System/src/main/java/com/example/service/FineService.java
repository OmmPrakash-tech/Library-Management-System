package com.example.service;

import java.util.List;

import com.example.domain.FineStatus;
import com.example.domain.FineType;
import com.example.payload.dto.FineDTO;
import com.example.payload.request.CreateFineRequest;
import com.example.payload.request.WaiveFineRequest;
import com.example.payload.response.PageResponse;
import com.example.payload.response.PaymentInitiateResponse;

public interface FineService {

    FineDTO createFine(CreateFineRequest request);

    PaymentInitiateResponse payFine(Long fineId);

    // Payment
    PaymentInitiateResponse initiatePayment(Long fineId);

    FineDTO applyPayment(Long fineId, Long amount, String transactionId);

    // Waiver
    FineDTO waiveFine(Long fineId, WaiveFineRequest request);

    // Fetch
    FineDTO getFineById(Long fineId);

    List<FineDTO> getMyFines(FineStatus status, FineType type);

    PageResponse<FineDTO> getAllFines(
            FineStatus status,
            FineType type,
            Long userId,
            int page,
            int size
    );
}