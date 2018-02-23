package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.dto.OfferDTO;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateFieldsValueValidator implements ConstraintValidator<DateFieldsValueConstraint, OfferDTO> {

    private final String startDateField = "startDate";
    private final String expiryDateField = "expiryDate";
    private final String isExpirableField = "isExpirable";

    @Override
    public void initialize(DateFieldsValueConstraint dateFieldsValueConstraint) {
    }

    @Override
    public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Long startDateFieldValue = (Long) beanWrapper.getPropertyValue(startDateField);
        Long expiyDateFieldValue = (Long) beanWrapper.getPropertyValue(expiryDateField);
        Boolean isExpirableFieldValue = (Boolean) beanWrapper.getPropertyValue(isExpirableField);
        boolean isValid = !isExpirableFieldValue || ((expiyDateFieldValue != null && startDateFieldValue != null) && startDateFieldValue <= expiyDateFieldValue);
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
