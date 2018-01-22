package com.ovoenergy.offer.validation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.ovoenergy.offer.validation.key.CodeKeys.NO_EXPIRITY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE;

@Constraint(validatedBy = ExpiryFieldsValueValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExpiryDateFieldsValueConstraint {

    String propertyPath();

    String message() default NO_EXPIRITY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}