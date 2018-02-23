package com.ovoenergy.offer.validation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.ovoenergy.offer.validation.key.CodeKeys.START_DATE_NOT_UPDATABLE;

@Constraint(validatedBy = StartDateNotUpdatableValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StartDateNotUpdatableConstraint {

    String message() default START_DATE_NOT_UPDATABLE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
