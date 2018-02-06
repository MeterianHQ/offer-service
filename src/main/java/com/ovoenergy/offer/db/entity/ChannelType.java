package com.ovoenergy.offer.db.entity;

public enum ChannelType {
    EMAIL("Email"),
    DISPLAY("Display"),
    SOCIAL("Social");

    String value;

    public static ChannelType byValue(String value) {
        for(ChannelType v : values()){
            if( v.value().equals(value)){
                return v;
            }
        }
        return null;
    }

    private ChannelType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
