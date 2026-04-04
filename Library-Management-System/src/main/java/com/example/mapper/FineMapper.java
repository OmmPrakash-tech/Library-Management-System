package com.example.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.domain.FineStatus;
import com.example.model.Fine;
import com.example.payload.dto.FineDTO;

@Component
public class FineMapper {

    public FineDTO toDTO(Fine fine) {

        if (fine == null) {
            return null;
        }

        FineDTO dto = new FineDTO();
        dto.setId(fine.getId());

        // ================= BOOK LOAN =================
        if (fine.getBookLoan() != null) {
            dto.setBookLoanId(fine.getBookLoan().getId());

            if (fine.getBookLoan().getBook() != null) {
                dto.setBookTitle(fine.getBookLoan().getBook().getTitle());
                dto.setBookIsbn(fine.getBookLoan().getBook().getIsbn());
            }
        }

        // ================= USER =================
        if (fine.getUser() != null) {
            dto.setUserId(fine.getUser().getId());
            dto.setUserName(fine.getUser().getFullName());
            dto.setUserEmail(fine.getUser().getEmail());
        }

        // ================= AMOUNT (SAFE BIGDECIMAL) =================
        BigDecimal amount = fine.getAmount() != null
                ? fine.getAmount()
                : BigDecimal.ZERO;

        BigDecimal paidAmount = fine.getPaidAmount() != null
                ? fine.getPaidAmount()
                : BigDecimal.ZERO;

        dto.setType(fine.getType());
        dto.setPaidAmount(paidAmount);

        // ✅ FIXED (BigDecimal subtraction)
        dto.setAmountOutstanding(amount.subtract(paidAmount));

        // ================= STATUS =================
        dto.setStatus(fine.getStatus());
        dto.setReason(fine.getReason());
        dto.setNote(fine.getNote());

        // ================= WAIVER =================
        if (fine.getWaivedBy() != null) {
            dto.setWaivedByUserId(fine.getWaivedBy().getId());
            dto.setWaivedByUserName(fine.getWaivedBy().getFullName());
        }

        dto.setWaivedAt(fine.getWaivedAt());
        dto.setWaiverReason(fine.getWaiverReason());

        // ================= PAYMENT =================
        dto.setPaidAt(fine.getPaidAt());

        if (fine.getProcessedBy() != null) {
            dto.setProcessedByUserId(fine.getProcessedBy().getId());
            dto.setProcessedByUserName(fine.getProcessedBy().getFullName());
        }

        dto.setTransactionId(fine.getTransactionId());

        // ================= AUDIT =================
        dto.setCreatedAt(fine.getCreatedAt());
        dto.setUpdatedAt(fine.getUpdatedAt());

        // ================= HELPER =================
        dto.setPayable(
                fine.getStatus() != FineStatus.PAID &&
                fine.getStatus() != FineStatus.WAIVED
        );

        return dto;
    }
}