package com.example.domain;

public enum FineStatus {

    PENDING("Pending"),
    PARTIALLY_PAID("Partially Paid"),
    PAID("Paid"),
    WAIVED("Waived");

    private final String displayName;

    FineStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
