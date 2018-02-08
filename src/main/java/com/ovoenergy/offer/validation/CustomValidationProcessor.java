package com.ovoenergy.offer.validation;

import com.google.common.collect.Sets;
import com.ovoenergy.offer.dto.*;
import com.ovoenergy.offer.validation.key.ValidationCodeMessageKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class CustomValidationProcessor {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    private MessageSource msgSource;

    public OfferValidationDTO processOfferValidation(OfferDTO request) {
        Set<ConstraintViolation<OfferDTO>> violations = validator.validate(request);
        if(violations == null || violations.size() == 0) {
            return null;
        }
        OfferValidationDTO validationDTO = new OfferValidationDTO(request);
        for (ConstraintViolation<OfferDTO> constraintViolation : violations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            propertyPath = propertyPath == null || propertyPath.isEmpty() ? "expiryDate" : propertyPath;
            String messageErrorCode = constraintViolation.getMessage();
            Set<ErrorMessageDTO> errorMessageDTOS = validationDTO.getConstraintViolations().get(propertyPath);
            errorMessageDTOS = errorMessageDTOS == null ? Sets.newHashSet() : errorMessageDTOS;
            errorMessageDTOS.add(
                    new ErrorMessageDTO(
                            messageErrorCode,
                            msgSource.getMessage(ValidationCodeMessageKeyPair.getMessageByCode(messageErrorCode), null, LocaleContextHolder.getLocale())));
            validationDTO.getConstraintViolations().put(propertyPath, errorMessageDTOS);
        }
        return validationDTO;
    }

    public Boolean isOfferVerifyInputDataValid(OfferVerifyDTO request) {
        Set<ConstraintViolation<OfferVerifyDTO>> violations = validator.validate(request);
        if(violations == null || violations.size() == 0) {
            return true;
        }
        return false;
    }

}
