package com.ovoenergy.offer.db.entity;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ChannelType {
    EMAIL("Email"),
    DISPLAY("Display"),
    SOCIAL("Social");

    private final String value;

    public String value() {
        return value;
    }

    public static ChannelType byValue(String value) {
        return Arrays.stream(values())
                .filter(v -> v.value().equals(value))
                .findFirst()
                .orElse(null);
    }
}
