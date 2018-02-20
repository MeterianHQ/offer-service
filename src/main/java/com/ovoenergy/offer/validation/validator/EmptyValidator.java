package com.ovoenergy.offer.validation.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmptyValidator implements ConstraintValidator<EmptyConstraint, String> {

    @Override
    public void initialize(EmptyConstraint contactNumber) {
    }

    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
        return StringUtils.isEmpty(contactField);
    }
}
