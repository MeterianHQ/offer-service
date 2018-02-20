package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateFieldsValueValidator implements ConstraintValidator<DateFieldsValueConstraint, OfferDTO> {

    private final String startDateField = "startDate";
    private final String expiryDateField = "expiryDate";
    private final String isExpirableField = "isExpirable";

    public void initialize(DateFieldsValueConstraint dateFieldsValueConstraint) {
    }

    public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Long startDateFieldValue = (Long) beanWrapper.getPropertyValue(startDateField);
        Long expiyDateFieldValue = (Long) beanWrapper.getPropertyValue(expiryDateField);
        Boolean isExpirableFieldValue = (Boolean) beanWrapper.getPropertyValue(isExpirableField);
        return !isExpirableFieldValue || ((expiyDateFieldValue != null && startDateFieldValue != null) && startDateFieldValue <= expiyDateFieldValue);
    }
}
