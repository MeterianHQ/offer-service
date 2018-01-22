package com.ovoenergy.offer.validation.key;

public interface MessageKeys {

    String FIELD_REQUIRED = "required.field";

    String NOT_NULL_FIELD = "not.null.field";

    String PROVIDED_VALUE_NOT_SUPPORTED = "provided.value.not.supported";

    String INPUT_VALUE_ZERO = "input.value.zero";

    String NOT_UNIQUE_OFFER_CODE = "not.unique.offer.code";

    String INVALID_OFFER_CODE = "invalid.offer.code";

    String NON_IN_FUTURE_DATE = "non.in.future.date";

    String OFFER_EXPIRY_DATE_BEFORE_START_DATE = "offer.expiry.date.before.start.date";

    String NO_EXPIRITY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE = "no.expirity.offer.could.not.have.expiry.date";

    interface Common {

        String GENERIC_SERVER_ERROR = "generic.server.error";

    }

}
