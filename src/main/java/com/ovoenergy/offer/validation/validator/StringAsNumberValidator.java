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

    @Override
    public void initialize(StringAsNumberConstraint constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.maxMessage = constraintAnnotation.maxMessage();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(CodeKeys.NOT_NULL_FIELD)
                    .addConstraintViolation();
            return false;
        }
        if (!NumberUtils.isNumber(value)) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(CodeKeys.INVALID_DATA_FORMAT)
                    .addConstraintViolation();
            return false;
        }
        long number = Long.parseLong(value);
        if (number < min) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(CodeKeys.INPUT_VALUE_ZERO)
                    .addConstraintViolation();
            return false;
        }
        if (number > max) {
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
