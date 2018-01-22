package com.ovoenergy.offer.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FutureDateValidator implements ConstraintValidator<FutureDateConstraint, Long> {

@Override
public void initialize(FutureDateConstraint contactNumber) {}

@Override
public boolean isValid(Long contactField, ConstraintValidatorContext cxt) {
        LocalDateTime localDateTime =  LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);
        return contactField == null || localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() <= contactField;
}

}