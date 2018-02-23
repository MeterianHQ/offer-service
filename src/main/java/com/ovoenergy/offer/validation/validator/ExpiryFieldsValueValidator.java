package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExpiryFieldsValueValidator implements ConstraintValidator<ExpiryDateFieldsValueConstraint, OfferDTO> {

    private final String expiryDateField = "expiryDate";
    private final String isExpirableField = "isExpirable";

    @Override
    public void initialize(ExpiryDateFieldsValueConstraint dateFieldsValueConstraint) {
    }

    @Override
    public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Long expiyDateFieldValue = (Long) beanWrapper.getPropertyValue(expiryDateField);
        Boolean isExpirableFieldValue = (Boolean) beanWrapper.getPropertyValue(isExpirableField);

        boolean isValid = (isExpirableFieldValue && expiyDateFieldValue != null) || (!isExpirableFieldValue && expiyDateFieldValue == null);
        if (!isValid) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(hibernateContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("expiryDate")
                    .addConstraintViolation();
        }
        return isValid;
    }

}