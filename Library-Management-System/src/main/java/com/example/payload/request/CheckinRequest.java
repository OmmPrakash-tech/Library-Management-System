package com.example.payload.request;

import com.example.domain.BookLoanStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckinRequest {

    @NotNull(message = "Book loan ID is mandatory")
    private Long bookLoanId;

    // Expected: RETURNED, LOST, DAMAGED
    @NotNull(message = "Condition/status is required")
    private BookLoanStatus condition = BookLoanStatus.RETURNED;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}