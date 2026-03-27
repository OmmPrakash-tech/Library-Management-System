package com.example.domain;

/**
 * Represents the type of action performed on a book loan.
 */
public enum BookLoanType {

    /**
     * Initial checkout of a book.
     */
    CHECKOUT,

    /**
     * Renewal of an existing loan.
     */
    RENEWAL,

    /**
     * Book return (check-in).
     */
    RETURN;

    /**
     * Check if this action modifies loan duration.
     */
    public boolean affectsDueDate() {
        return this == CHECKOUT || this == RENEWAL;
    }
}