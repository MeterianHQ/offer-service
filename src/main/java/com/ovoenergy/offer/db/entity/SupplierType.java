package com.ovoenergy.offer.db.entity;

public enum SupplierType {

    AMAZON("Amazon");

    String value;

    private SupplierType(String value) {
        this.value = value;
    }

    public static SupplierType byValue(String value) {
        for(SupplierType v : values()){
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
