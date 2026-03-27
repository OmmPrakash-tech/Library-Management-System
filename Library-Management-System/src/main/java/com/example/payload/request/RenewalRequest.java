package com.example.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenewalRequest {

    @NotNull(message = "Book loan ID is mandatory")
    private Long bookLoanId;

    @NotNull(message = "Extension days is required")
    @Min(value = 1, message = "Extension days must be at least 1")
    private Integer extensionDays = 14; // Default: 14 days

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}