package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.validation.key.CodeKeys;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ovoenergy.offer.validation.key.CodeKeys.INVALID_DATA_FORMAT;

@Constraint(validatedBy = StringAsNumberValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@NotEmpty(message = CodeKeys.NOT_NULL_FIELD)
@Documented
public @interface StringAsNumberConstraint {

    String message() default INVALID_DATA_FORMAT;

    long min();

    long max();

    String maxMessage();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
