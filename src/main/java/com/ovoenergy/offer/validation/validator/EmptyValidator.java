package com.ovoenergy.offer.validation.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class EmptyValidator implements ConstraintValidator<EmptyConstraint, String> {

        @Override
        public void initialize(EmptyConstraint contactNumber) {}

        @Override
        public boolean isValid(String contactField, ConstraintValidatorContext cxt) {
                return contactField == null || StringUtils.isEmpty(contactField);
        }
}
