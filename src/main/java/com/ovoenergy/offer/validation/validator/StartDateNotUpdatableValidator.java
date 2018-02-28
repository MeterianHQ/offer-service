package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.db.entity.OfferDBEntity;
import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferDTO;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

@Component
public class StartDateNotUpdatableValidator implements ConstraintValidator<StartDateNotUpdatableConstraint, OfferDTO> {

    private final OfferRepository offerRepository;

    public StartDateNotUpdatableValidator(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public void initialize(StartDateNotUpdatableConstraint constraint) {
    }

    @Override
    public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {
        if (value.getId() == null) {
            return true;
        }
        OfferDBEntity offerDBEntity = offerRepository.findOne(value.getId());
        if (offerDBEntity == null) {
            return true;
        }
        boolean equals = Objects.equals(offerDBEntity.getStartDate(), value.getStartDate());
        if (!StatusType.DRAFT.equals(offerDBEntity.getStatus()) && !equals) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .buildConstraintViolationWithTemplate(hibernateContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("startDate")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
