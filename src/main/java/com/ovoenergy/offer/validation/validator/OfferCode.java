package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.db.entity.StatusType;
import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class OfferCode implements ConstraintValidator<OfferCodeConstraint, OfferDTO> {

    private final OfferRepository offerRepository;

    @Autowired
    public OfferCode(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public void initialize(OfferCodeConstraint constraint) {
    }

    @Override
    public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {
        if (value.getId() == null) {
            return offerRepository.findOneByOfferCodeIgnoreCaseAndStatus(value.getOfferCode(), StatusType.ACTIVE) == null;
        } else {
            return !offerRepository.existsByOfferCodeIgnoreCaseAndStatusAndIdIsNot(value.getOfferCode(), null, value.getId());
        }
    }
}
