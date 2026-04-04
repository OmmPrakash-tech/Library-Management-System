package com.example.payload.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.domain.FineStatus;
import com.example.domain.FineType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FineDTO {

    private Long id;

    @NotNull(message = "Book loan ID is mandatory")
    private Long bookLoanId;

    private String bookTitle;
    private String bookIsbn;

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private String userName;
    private String userEmail;

    @NotNull(message = "Fine type is mandatory")
    private FineType type;

    

    private BigDecimal paidAmount;
    private BigDecimal amountOutstanding;

    private FineStatus status;

    private String reason;
    private String note;

    // Waiver
    private Long waivedByUserId;
    private String waivedByUserName;
    private LocalDateTime waivedAt;
    private String waiverReason;

    // Payment
    private LocalDateTime paidAt;
    private Long processedByUserId;
    private String processedByUserName;
    private String transactionId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper
    private boolean payable;
}