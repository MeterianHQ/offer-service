package com.ovoenergy.offer.validation.validator;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.ovoenergy.offer.validation.key.CodeKeys.ENTITY_NOT_EXIST;

@Constraint(validatedBy = EntityExistsValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityExistsConstraint {
    String message() default ENTITY_NOT_EXIST;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends JpaRepository<?, Long>> repository();
}
