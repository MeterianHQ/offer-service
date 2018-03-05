package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.validation.key.CodeKeys;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringAsNumberValidator implements ConstraintValidator<StringAsNumberConstraint, String> {

    private long min;
    private long max;
    private String maxMessage;
    private String message;

    @Override
    public void initialize(StringAsNumberConstraint constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.maxMessage = constraintAnnotation.maxMessage();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(CodeKeys.FIELD_REQUIRED)
                    .addConstraintViolation();
            return false;
        }
        if (!NumberUtils.isNumber(value) || Long.parseLong(value) < min) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
        if (Long.parseLong(value) > max) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(maxMessage)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
