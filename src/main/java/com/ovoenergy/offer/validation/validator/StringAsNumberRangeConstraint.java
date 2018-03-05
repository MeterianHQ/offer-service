package com.ovoenergy.offer.validation.validator;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotEmpty
@Pattern(regexp = "")
@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringAsNumberRangeConstraint {

    @OverridesAttribute(constraint = Pattern.class, name = "regexp") String regexp();

    @OverridesAttribute(constraint = Pattern.class, name = "message") String message();

    @OverridesAttribute(constraint = NotEmpty.class, name = "message") String notEmpMessage();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
