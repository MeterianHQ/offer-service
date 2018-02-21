package com.ovoenergy.offer.db.entity;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum EligibilityCriteriaType {

    SSD("SSD");

    private final String value;

    public String value() {
        return value;
    }

    public static EligibilityCriteriaType byValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.value().equals(value))
                .findFirst()
                .orElse(null);
    }
}
