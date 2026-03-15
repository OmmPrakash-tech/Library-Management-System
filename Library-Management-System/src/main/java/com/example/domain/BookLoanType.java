package com.example.domain;

public enum BookLoanType {

    /**
 * Regular checkout (book loan initiated)
 */
CHECKOUT,

/**
 * Book renewal (extending due date)
 */
RENEWAL,

/**
 * Book return (check-in)
 */
RETURN

}
