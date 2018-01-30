package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateFieldsValueValidator implements ConstraintValidator<DateFieldsValueConstraint, OfferDTO> {

private String startDateField = "startDate";

private String expiryDateField = "expiryDate";

private String isExpirableField = "isExpirable";

public void initialize(DateFieldsValueConstraint dateFieldsValueConstraint) {
}

public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {

        Long startDateFieldValue = (Long) new BeanWrapperImpl(value)
        .getPropertyValue(startDateField);
        Long expiyDateFieldValue = (Long) new BeanWrapperImpl(value)
        .getPropertyValue(expiryDateField);
        Boolean isExpirableFieldValue =  (Boolean) new BeanWrapperImpl(value)
                .getPropertyValue(isExpirableField);
         return !isExpirableFieldValue || ((expiyDateFieldValue != null && startDateFieldValue != null) && startDateFieldValue < expiyDateFieldValue);
}

}
