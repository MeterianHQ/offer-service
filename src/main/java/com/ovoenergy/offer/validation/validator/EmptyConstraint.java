package com.ovoenergy.offer.validation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.ovoenergy.offer.validation.key.CodeKeys.FIELD_REQUIRED;

@Documented
@Constraint(validatedBy = EmptyValidator.class)
@Target( {ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EmptyConstraint {
    String message() default FIELD_REQUIRED;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}