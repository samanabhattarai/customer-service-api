package com.samana.customerservice.utils;

public enum CustomerTier {
    SILVER("Silver"),
    GOLD("Gold"),
    PLATINUM("Platinum");

    private final String value;

    CustomerTier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
