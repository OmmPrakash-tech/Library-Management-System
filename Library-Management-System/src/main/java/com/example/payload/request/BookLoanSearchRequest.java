package com.example.payload.request;

import java.time.LocalDate;

import com.example.domain.BookLoanStatus;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanSearchRequest {

    // ================= FILTERS =================
    private Long userId;
    private Long bookId;
    private BookLoanStatus status;

    private boolean overdueOnly = false;
    private boolean unpaidFinesOnly = false;

    private LocalDate startDate;
    private LocalDate endDate;

    // ================= PAGINATION =================
    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 20;

    // ================= SORTING =================
    private String sortBy = "createdAt";   // can be validated in service
    private String sortDirection = "DESC";

    // ================= VALIDATION =================
    @AssertTrue(message = "startDate must be before or equal to endDate")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) return true;
        return !startDate.isAfter(endDate);
    }
}