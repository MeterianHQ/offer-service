package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExpiryFieldsValueValidator implements ConstraintValidator<ExpiryDateFieldsValueConstraint, OfferDTO> {

        private String expiryDateField = "expiryDate";

        private String isExpirableField = "isExpirable";


public void initialize(ExpiryDateFieldsValueConstraint dateFieldsValueConstraint) {
}

public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {

        Long expiyDateFieldValue = (Long) new BeanWrapperImpl(value)
        .getPropertyValue(expiryDateField);
        Boolean isExpirableFieldValue =  (Boolean) new BeanWrapperImpl(value)
                .getPropertyValue(isExpirableField);

        return isExpirableFieldValue && expiyDateFieldValue != null;
}

}
