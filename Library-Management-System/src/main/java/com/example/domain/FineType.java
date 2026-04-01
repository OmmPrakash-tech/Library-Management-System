package com.example.domain;

public enum FineType {

    OVERDUE("Overdue"),
    DAMAGE("Damage"),
    LOSS("Loss"),
    PROCESSING("Processing Fee");

    private final String displayName;

    FineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}