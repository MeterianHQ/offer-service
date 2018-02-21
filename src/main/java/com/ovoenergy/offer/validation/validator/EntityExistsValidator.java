package com.ovoenergy.offer.validation.validator;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class EntityExistsValidator implements ConstraintValidator<EntityExistsConstraint, Long> {

    private final ApplicationContext applicationContext;
    private JpaRepository<?, Long> repository;

    @Autowired
    public EntityExistsValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(EntityExistsConstraint constraintAnnotation) {
        repository = applicationContext.getBean(constraintAnnotation.repository());
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else {
            boolean exists = repository.exists(value);
            if (exists) {
                return true;
            } else {
                HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
                hibernateContext.disableDefaultConstraintViolation();
                hibernateContext
                        .buildConstraintViolationWithTemplate(hibernateContext.getDefaultConstraintMessageTemplate())
                        .addConstraintViolation();
                return false;
            }
        }
    }
}
