package com.example.domain;

/**
 * Represents the current lifecycle status of a book loan.
 */
public enum BookLoanStatus {

    CHECKED_OUT,

    RETURN_REQUESTED, // ✅ ADD THIS

    RETURNED,

    OVERDUE,

    LOST,

    DAMAGED;

    public boolean isActive() {
        return this == CHECKED_OUT || this == OVERDUE || this == RETURN_REQUESTED;
    }

    public boolean isClosed() {
        return this == RETURNED || this == LOST || this == DAMAGED;
    }
}