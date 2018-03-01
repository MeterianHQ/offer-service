package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.validation.key.CodeKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ovoenergy.offer.validation.key.CodeKeys.ENTITY_NOT_EXIST;

@Constraint(validatedBy = EntityExistsValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@NotNull(message = CodeKeys.NOT_NULL_FIELD)
public @interface EntityExistsConstraint {
    String message() default ENTITY_NOT_EXIST;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends JpaRepository<?, Long>> repository();
}
