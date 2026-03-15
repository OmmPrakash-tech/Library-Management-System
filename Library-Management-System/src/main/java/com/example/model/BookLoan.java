package com.example.model;



import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.domain.BookLoanStatus;
import com.example.domain.BookLoanType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(nullable = false)
@ManyToOne
private User user;

@JoinColumn(nullable = false)
@ManyToOne
private Book book;

private BookLoanType type;

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private BookLoanStatus status;



 @Column(nullable = false)
private LocalDate checkoutDate;

private LocalDate dueDate;

@Column(nullable = false)
private LocalDate returnDate;

@Column(nullable = false)
private Integer renewalCount = 0;

@Column(nullable = false)
private Integer maxRenewals = 2;

// fine todo

@Column(length= 500)
private String notes;

@Column(nullable = false)
private Boolean isOverdue = false;

@Column(nullable = false)
private Integer overdueDays = 0;

@Column(nullable = false, updatable = false)
@CreationTimestamp
private LocalDateTime createdAt;

@Column(nullable = false)
@UpdateTimestamp
private LocalDateTime updatedAt;

public boolean isActive() {
    return status == BookLoanStatus.CHECKED_OUT
            || status == BookLoanStatus.OVERDUE;
}

public boolean canRenew() {
    return status == BookLoanStatus.CHECKED_OUT
            && !isOverdue
            && renewalCount < maxRenewals;
}



}
