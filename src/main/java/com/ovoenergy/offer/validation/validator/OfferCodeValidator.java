package com.ovoenergy.offer.validation.validator;

import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.dto.OfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class OfferCodeValidator implements ConstraintValidator<OfferCodeConstraint, OfferDTO> {

    private final OfferRepository offerRepository;

    @Autowired
    public OfferCodeValidator(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public void initialize(OfferCodeConstraint constraint) {
    }

    @Override
    public boolean isValid(OfferDTO value, ConstraintValidatorContext context) {
        return value.getId() == null
                ? offerRepository.findOneByOfferCodeIgnoreCase(value.getOfferCode()) == null
                : !offerRepository.existsByOfferCodeIgnoreCaseAndIdIsNot(value.getOfferCode(), value.getId());
    }
}
