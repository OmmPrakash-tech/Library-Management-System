package com.example.payload.request;

import java.time.LocalDate;

import com.example.domain.BookLoanStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookLoanSearchRequest {

    private Long userId;
    private Long bookId;
    private BookLoanStatus status;

    private Boolean overdueOnly = false;
    private Boolean unpaidFinesOnly = false;

    private LocalDate startDate;
    private LocalDate endDate;

    @Min(0)
    private Integer page = 0;

    @Min(1)
    @Max(100)
    private Integer size = 20;

    @Pattern(
        regexp = "createdAt|dueDate|checkoutDate|status",
        message = "Invalid sort field"
    )
    private String sortBy = "createdAt";

    @Pattern(
        regexp = "ASC|DESC",
        message = "Sort direction must be ASC or DESC"
    )
    private String sortDirection = "DESC";
}