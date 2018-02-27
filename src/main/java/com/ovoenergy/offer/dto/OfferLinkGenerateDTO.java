package com.ovoenergy.offer.dto;

import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.validator.EntityExistsConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OfferLinkGenerateDTO {

    @EntityExistsConstraint(repository = OfferRepository.class)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    private Long offerId;

    @NotEmpty(message = CodeKeys.INVALID_EMAIL)
    @Email(message = CodeKeys.INVALID_EMAIL)
    private String email;
}
