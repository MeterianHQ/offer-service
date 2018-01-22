package com.ovoenergy.offer.validation.key;

public interface CodeKeys {

    String GENERIC_SERVER_ERROR = "OFR1";

    String FIELD_REQUIRED = "OFR2";

    String NOT_NULL_FIELD = "OFR3";

    String PROVIDED_VALUE_NOT_SUPPORTED = "OFR4";

    String INPUT_VALUE_ZERO = "OFR5";

    //TODO: Add validators
    String NOT_UNIQUE_OFFER_CODE = "OFR6";

    String INVALID_OFFER_CODE = "OFR7";

    String NON_IN_FUTURE_DATE = "OFR8";

    String OFFER_EXPIRY_DATE_BEFORE_START_DATE = "OFR9";

    String NO_EXPIRITY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE = "OFR10";

}
