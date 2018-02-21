package com.ovoenergy.offer.validation.key;

import com.ovoenergy.offer.exception.NotSupportedErrorCodeException;

import java.util.Arrays;

public enum ValidationCodeMessageKeyPair {

    FIELD_REQUIRED(CodeKeys.FIELD_REQUIRED, MessageKeys.FIELD_REQUIRED),

    NOT_NULL_FIELD(CodeKeys.NOT_NULL_FIELD, MessageKeys.NOT_NULL_FIELD),

    PROVIDED_VALUE_NOT_SUPPORTED(CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED, MessageKeys.PROVIDED_VALUE_NOT_SUPPORTED),

    INPUT_VALUE_ZERO(CodeKeys.INPUT_VALUE_ZERO, MessageKeys.INPUT_VALUE_ZERO),

    NOT_UNIQUE_OFFER_CODE(CodeKeys.NOT_UNIQUE_OFFER_CODE, MessageKeys.NOT_UNIQUE_OFFER_CODE),

    INVALID_OFFER_CODE(CodeKeys.INVALID_OFFER_CODE, MessageKeys.INVALID_OFFER_CODE),

    NON_IN_FUTURE_DATE(CodeKeys.NON_IN_FUTURE_DATE, MessageKeys.NON_IN_FUTURE_DATE),

    OFFER_EXPIRY_DATE_BEFORE_START_DATE(CodeKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE, MessageKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE),

    NO_EXPIRY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE(CodeKeys.NO_EXPIRY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE, MessageKeys.NO_EXPIRY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE),

    INPUT_REDEMPTION_MAX(CodeKeys.INPUT_REDEMPTION_MAX, MessageKeys.INPUT_REDEMPTION_MAX),

    INPUT_VALUE_MAX(CodeKeys.INPUT_VALUE_MAX, MessageKeys.INPUT_VALUE_MAX),

    INVALID_EMAIL(CodeKeys.INVALID_EMAIL, MessageKeys.INVALID_EMAIL),

    OFFER_EXPIRED(CodeKeys.OFFER_EXPIRED, MessageKeys.OFFER_EXPIRED),

    OFFER_INVALID(CodeKeys.OFFER_INVALID, MessageKeys.OFFER_INVALID),

    OFFER_STATUS_TYPE_NOT_VALID(CodeKeys.OFFER_STATUS_TYPE_NOT_VALID, MessageKeys.OFFER_STATUS_TYPE_NOT_VALID),

    NULL_FIELD(CodeKeys.NULL_FIELD, MessageKeys.NULL_FIELD);

    private String code;

    private String messageKey;

    ValidationCodeMessageKeyPair(String code, String messageKey) {
        this.code = code;
        this.messageKey  = messageKey;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public static String getMessageByCode(String code) {
        ValidationCodeMessageKeyPair actualPair = Arrays.stream(ValidationCodeMessageKeyPair.values())
                .filter(mp -> code.equals(mp.getCode()))
                .findFirst().orElseThrow( () ->  new NotSupportedErrorCodeException("Not Supported Error Code"));
        return  actualPair.getMessageKey();
    }
}