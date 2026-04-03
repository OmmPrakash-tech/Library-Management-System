package com.example.model;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.domain.BookLoanStatus;
import com.example.domain.BookLoanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "book_loan",
    indexes = {
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_book", columnList = "book_id"),
        @Index(name = "idx_status", columnList = "status")
    }
)
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= RELATIONSHIPS =================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // ================= ENUMS =================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookLoanType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookLoanStatus status;

    // ================= DATES =================
    @Column(nullable = false)
    private LocalDate checkoutDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    private boolean returnRequested;

    // ================= RENEWAL =================
    @Column(nullable = false)
    private Integer renewalCount = 0;

    @Column(nullable = false)
    private Integer maxRenewals = 2;

    // ================= EXTRA =================
    @Column(length = 500)
    private String notes;

    // ================= AUDIT =================
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ================= BUSINESS LOGIC =================

    public boolean isActive() {
        return status == BookLoanStatus.CHECKED_OUT
                || status == BookLoanStatus.OVERDUE;
    }

    public boolean isOverdue() {
        return status == BookLoanStatus.CHECKED_OUT
                && dueDate != null
                && LocalDate.now().isAfter(dueDate);
    }

    public int getOverdueDays() {
        if (!isOverdue()) return 0;
        return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public boolean canRenew() {
        return status == BookLoanStatus.CHECKED_OUT
                && !isOverdue()
                && renewalCount < maxRenewals;
    }

    public void renewLoan(int extraDays) {
        if (!canRenew()) {
            throw new IllegalStateException("Loan cannot be renewed");
        }
        this.dueDate = this.dueDate.plusDays(extraDays);
        this.renewalCount++;
    }

    public void markAsReturned() {
        this.status = BookLoanStatus.RETURNED;
        this.returnDate = LocalDate.now();
    }

    public void markAsOverdue() {
        if (isOverdue()) {
            this.status = BookLoanStatus.OVERDUE;
        }
    }

    public void setDefaultDueDate(int days) {
        if (this.checkoutDate == null) {
            this.checkoutDate = LocalDate.now();
        }
        this.dueDate = this.checkoutDate.plusDays(days);
    }

    public double calculateFine(double finePerDay) {
        return getOverdueDays() * finePerDay;
    }
}