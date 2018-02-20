package com.ovoenergy.offer.validation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ovoenergy.offer.validation.key.CodeKeys.NON_IN_FUTURE_DATE;

@Documented
@Constraint(validatedBy = FutureDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDateConstraint {
    String message() default NON_IN_FUTURE_DATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}