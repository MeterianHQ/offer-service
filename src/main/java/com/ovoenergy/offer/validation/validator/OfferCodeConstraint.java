package com.ovoenergy.offer.validation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ovoenergy.offer.validation.key.CodeKeys.NOT_UNIQUE_OFFER_CODE;

@Documented
@Constraint(validatedBy = OfferCode.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OfferCodeConstraint {
    String message() default NOT_UNIQUE_OFFER_CODE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
