package com.ovoenergy.offer.validation;

import com.google.common.collect.Sets;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.validation.group.BaseOfferChecks;
import com.ovoenergy.offer.validation.group.EmptyDraftOfferChecks;
import com.ovoenergy.offer.validation.group.NonEmptyDraftOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredActiveOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredDraftOfferChecks;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.key.ValidationCodeMessageKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomValidationProcessor {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    private MessageSource msgSource;

    public OfferValidationDTO processActiveOfferInputDataValidationViolations(OfferDTO request) {
        Set<ConstraintViolation<OfferDTO>> violations = validator.validate(request, BaseOfferChecks.class, RequiredActiveOfferChecks.class);
        if (violations.size() == 0) {
            return null;
        }
        return prepareValidationDTO(request, violations);
    }

    public OfferValidationDTO processDraftOfferInputDataValidationViolations(OfferDTO request) {
        Set<ConstraintViolation<OfferDTO>> emptyFieldsViolations = validator.validate(request, EmptyDraftOfferChecks.class);
        Set<ConstraintViolation<OfferDTO>> violations = Sets.newHashSet();
        if (emptyFieldsViolations == null || emptyFieldsViolations.size() == 0) {
            Set<ConstraintViolation<OfferDTO>> nonEmptyFieldsViolations = emptyFieldsViolations = validator.validate(request, NonEmptyDraftOfferChecks.class);
            Set<String> emptyFieldsToSkip = emptyFieldsViolations.stream().map(cv -> cv.getPropertyPath().toString()).collect(Collectors.toSet());
            violations = nonEmptyFieldsViolations.stream().filter(cv -> emptyFieldsToSkip.contains(cv.getPropertyPath().toString())).collect(Collectors.toSet());
        }
        violations.addAll(validator.validate(request, BaseOfferChecks.class, RequiredDraftOfferChecks.class));

        if (violations.size() == 0) {
            return null;
        }
        return prepareValidationDTO(request, violations);
    }

    private OfferValidationDTO prepareValidationDTO(OfferDTO request, Set<ConstraintViolation<OfferDTO>> violations) {
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

    public <T> void processOfferInputDataInvalidOfferException(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (violations != null && violations.size() > 0) {
            throw new VariableNotValidException(CodeKeys.OFFER_INVALID);
        }
    }

    public <T> void processOfferInputDataValidationException(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (violations != null && violations.size() > 0) {
            String messageErrorCode = violations.iterator().next().getMessage();
            throw new VariableNotValidException(messageErrorCode);
        }
    }

}
