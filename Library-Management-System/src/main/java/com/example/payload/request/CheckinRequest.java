package com.example.payload.request;

import com.example.domain.BookLoanStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckinRequest {

    @NotNull(message = "Book loan ID is mandatory")
    private Long bookLoanId;

    private BookLoanStatus condition = BookLoanStatus.RETURNED; // RETURNED, LOST, DAMAGED

    private String notes;
}
