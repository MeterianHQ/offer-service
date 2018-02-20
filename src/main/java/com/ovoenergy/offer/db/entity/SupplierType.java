package com.ovoenergy.offer.db.entity;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum SupplierType {

    AMAZON("Amazon");

    private final String value;

    public String value() {
        return value;
    }

    public static SupplierType byValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.value().equals(value))
                .findFirst()
                .orElse(null);
    }
}
