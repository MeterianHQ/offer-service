package com.ovoenergy.offer.validation;

import com.google.common.collect.Sets;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.dto.ErrorMessageDTO;
import com.ovoenergy.offer.dto.OfferDTO;
import com.ovoenergy.offer.dto.OfferValidationDTO;
import com.ovoenergy.offer.exception.RequestIdsInValidException;
import com.ovoenergy.offer.exception.VariableNotValidException;
import com.ovoenergy.offer.validation.group.BaseOfferChecks;
import com.ovoenergy.offer.validation.group.EmptyDraftOfferChecks;
import com.ovoenergy.offer.validation.group.NonEmptyDraftCreateOfferChecks;
import com.ovoenergy.offer.validation.group.NonEmptyDraftOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredActiveOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredCreateActiveOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredDraftOfferChecks;
import com.ovoenergy.offer.validation.group.RequiredOfferCreateChecks;
import com.ovoenergy.offer.validation.group.RequiredOfferUpdateChecks;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.key.ValidationCodeMessageKeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomValidationProcessor {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    private MessageSource msgSource;

    public OfferValidationDTO processOfferCreateValidation(OfferDTO request) {
        if (StatusType.DRAFT.name().equalsIgnoreCase(request.getStatus())) {
            Class[] notEmptyGroups = {NonEmptyDraftOfferChecks.class, NonEmptyDraftCreateOfferChecks.class};
            Class[] validateGroups = {BaseOfferChecks.class, RequiredDraftOfferChecks.class, RequiredOfferCreateChecks.class};
            return processDraftOfferValidation(request, notEmptyGroups, validateGroups);
        } else {
            return processActiveOfferValidation(request, BaseOfferChecks.class, RequiredActiveOfferChecks.class, RequiredCreateActiveOfferChecks.class, RequiredOfferCreateChecks.class);
        }
    }

    public OfferValidationDTO processOfferUpdateValidation(OfferDTO request, Long id) {
        if (!Objects.equals(request.getId(), id)) {
            throw new RequestIdsInValidException(CodeKeys.PROVIDED_TWO_DIFFERENT_IDS);
        }
        if (StatusType.DRAFT.name().equalsIgnoreCase(request.getStatus())) {
            Class[] notEmptyGroups = {NonEmptyDraftOfferChecks.class};
            Class[] validateGroups = {RequiredOfferUpdateChecks.class, BaseOfferChecks.class, RequiredDraftOfferChecks.class};
            return processDraftOfferValidation(request, notEmptyGroups, validateGroups);
        } else {
            return processActiveOfferValidation(request, RequiredOfferUpdateChecks.class, BaseOfferChecks.class, RequiredActiveOfferChecks.class);
        }
    }

    private OfferValidationDTO processActiveOfferValidation(OfferDTO request, Class<?>... groups) {
        Set<ConstraintViolation<OfferDTO>> violations = validator.validate(request, groups);
        if (violations.size() == 0) {
            return null;
        }
        return prepareValidationDTO(request, violations);
    }

    private OfferValidationDTO processDraftOfferValidation(OfferDTO request, Class<?>[] notEmptyGroups, Class<?>[] validateGroups) {
        Set<ConstraintViolation<OfferDTO>> emptyFieldsViolations = validator.validate(request, EmptyDraftOfferChecks.class);
        Set<ConstraintViolation<OfferDTO>> violations = Sets.newHashSet();
        if (emptyFieldsViolations.size() > 0) {
            Set<ConstraintViolation<OfferDTO>> nonEmptyFieldsViolations = validator.validate(request, notEmptyGroups);
            Set<String> emptyFieldsToSkip = emptyFieldsViolations.stream().map(cv -> cv.getPropertyPath().toString()).collect(Collectors.toSet());
            violations = nonEmptyFieldsViolations.stream().filter(cv -> emptyFieldsToSkip.contains(cv.getPropertyPath().toString())).collect(Collectors.toSet());
        }
        violations.addAll(validator.validate(request, validateGroups));

        if (violations.size() == 0) {
            return null;
        }
        return prepareValidationDTO(request, violations);
    }

    private OfferValidationDTO prepareValidationDTO(OfferDTO request, Set<ConstraintViolation<OfferDTO>> violations) {
        OfferValidationDTO validationDTO = new OfferValidationDTO(request);
        for (ConstraintViolation<OfferDTO> constraintViolation : violations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String messageErrorCode = constraintViolation.getMessage();
            validationDTO.getConstraintViolations().computeIfAbsent(propertyPath, s -> Sets.newHashSet())
                    .add(new ErrorMessageDTO(messageErrorCode,
                            msgSource.getMessage(ValidationCodeMessageKeyPair.getMessageByCode(messageErrorCode), null, LocaleContextHolder.getLocale())));
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
