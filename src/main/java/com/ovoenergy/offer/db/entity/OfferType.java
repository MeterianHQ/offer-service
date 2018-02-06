package com.ovoenergy.offer.db.entity;

public enum OfferType {

    GIFTCARD("Giftcard");

    String value;

    private OfferType(String value) {
        this.value = value;
    }

    public static OfferType byValue(String value) {
        for(OfferType v : values()){
            if( v.value().equals(value)){
                return v;
            }
        }
        return null;
    }

    public String value() {
        return value;
    }
}
