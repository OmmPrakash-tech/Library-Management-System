package com.example.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequestDTO {

    private Long bookLoanId;   // ✅ required
    private String notes;      // ✅ optional

}
