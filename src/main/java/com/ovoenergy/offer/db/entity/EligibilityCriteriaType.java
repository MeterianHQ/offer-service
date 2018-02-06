package com.ovoenergy.offer.db.entity;

public enum EligibilityCriteriaType {

    SSD("SSD");

    String value;

    private EligibilityCriteriaType(String value) {
        this.value = value;
    }

    public static EligibilityCriteriaType byValue(String value) {
        for(EligibilityCriteriaType v : values()){
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
