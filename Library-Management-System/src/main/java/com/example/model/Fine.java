package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.domain.FineStatus;
import com.example.domain.FineType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    // ✅ FIXED
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(nullable = false)
    private BookLoan bookLoan;

    private FineType type;

    @Column(nullable = false)
    private BigDecimal amount;

    private FineStatus status;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String note;

    @ManyToOne
    private User waivedBy;

    @Column(name = "waived_at")
    private LocalDateTime waivedAt;

    @Column(name = "waiver_reason", length = 500)
    private String waiverReason;

    // Payment tracking
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    private User processedBy;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ✅ FIXED METHOD
    public void applyPayment(BigDecimal paymentAmount) {

        // ✅ validation
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        if (this.status == FineStatus.WAIVED) {
            throw new IllegalStateException("Cannot pay a waived fine");
        }

        // ✅ correct addition
        BigDecimal newPaidAmount = this.paidAmount.add(paymentAmount);

        // ✅ prevent overpayment
        if (newPaidAmount.compareTo(this.amount) > 0) {
            throw new IllegalArgumentException("Payment exceeds fine amount");
        }

        this.paidAmount = newPaidAmount;

        // ✅ correct comparison
        if (this.paidAmount.compareTo(this.amount) == 0) {
            this.status = FineStatus.PAID;
            this.paidAt = LocalDateTime.now();
        } else {
            this.status = FineStatus.PARTIALLY_PAID;
        }
    }

    public void waive(User admin, String reason) {

        if (this.status == FineStatus.PAID) {
            throw new IllegalStateException("Cannot waive a paid fine");
        }

        this.status = FineStatus.WAIVED;
        this.waivedBy = admin;
        this.waivedAt = LocalDateTime.now();
        this.waiverReason = reason;
    }
}