package com.ovoenergy.offer.validation.key;

public interface MessageKeys {

    String FIELD_REQUIRED = "required.field";

    String NOT_NULL_FIELD = "not.null.field";

    String PROVIDED_VALUE_NOT_SUPPORTED = "provided.value.not.supported";

    String NOT_UNIQUE_OFFER_CODE = "not.unique.offer.code";

    String INVALID_OFFER_CODE = "invalid.offer.code";

    String NON_IN_FUTURE_DATE = "non.in.future.date";

    String OFFER_EXPIRY_DATE_BEFORE_START_DATE = "offer.expiry.date.before.start.date";

    String NO_EXPIRY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE = "no.expiry.offer.could.not.have.expiry.date";

    String INVALID_EMAIL = "invalid.email";

    String OFFER_INVALID = "offer.invalid";

    String OFFER_EXPIRED = "offer.expired";

    String OFFER_STATUS_TYPE_NOT_VALID = "offer.status.type.not.valid";

    String NULL_FIELD = "null.field";

    String OFFER_CODE_FIELD_SIZE = "size.offer.code.field";

    String OFFER_NAME_FIELD_SIZE = "size.offer.name.field";

    String OFFER_DESCRIPTION_FIELD_SIZE = "size.offer.description.field";

    String PROVIDED_TWO_DIFFERENT_IDS = "provided.ids.different";

    String ENTITY_NOT_EXIST = "entity.not.exist";

    String START_DATE_NOT_UPDATABLE = "start.date.not.updatable";

    String OFFER_LINK_EXPIRED = "offer.link.expired";

    String INVALID_VALUE_FORMAT = "invalid.value.format";

    String INVALID_REDEMPTION_FORMAT = "invalid.redemption.format";

    interface Common {

        String GENERIC_SERVER_ERROR = "generic.server.error";

    }

}
