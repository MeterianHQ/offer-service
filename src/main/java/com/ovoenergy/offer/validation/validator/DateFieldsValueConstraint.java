package com.ovoenergy.offer.validation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.ovoenergy.offer.validation.key.CodeKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE;

@Constraint(validatedBy = DateFieldsValueValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateFieldsValueConstraint {

    String propertyPath();

    String message() default OFFER_EXPIRY_DATE_BEFORE_START_DATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
