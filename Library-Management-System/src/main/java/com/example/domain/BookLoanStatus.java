package com.example.domain;

/**
 * Represents the current lifecycle status of a book loan.
 */
public enum BookLoanStatus {

    /**
     * Book is currently issued to the user.
     */
    CHECKED_OUT,

    /**
     * Book has been returned successfully.
     */
    RETURNED,

    /**
     * Loan has passed its due date and is not yet returned.
     */
    OVERDUE,

    /**
     * Book is reported lost by the user.
     */
    LOST,

    /**
     * Book is returned but found damaged.
     */
    DAMAGED;

    /**
     * Check if loan is active (still in possession of user).
     */
    public boolean isActive() {
        return this == CHECKED_OUT || this == OVERDUE;
    }

    /**
     * Check if loan is closed.
     */
    public boolean isClosed() {
        return this == RETURNED || this == LOST || this == DAMAGED;
    }
}