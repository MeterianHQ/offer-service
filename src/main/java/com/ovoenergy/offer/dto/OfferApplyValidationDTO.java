package com.ovoenergy.offer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiModel(value = "Offer Apply Validation", description = "Validation information about every field that failed to pass internal validation system for offer apply operation")
@JsonSerialize
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class OfferApplyValidationDTO extends OfferApplyDTO {

    @ApiModelProperty(name = "constraintViolations", required = true)
    private Map<String, Set<ErrorMessageDTO>> constraintViolations =  new HashMap<>();

    public OfferApplyValidationDTO(OfferApplyDTO offerDTO) {
        super();
        this.setOfferCode(offerDTO.getOfferCode());
    }
}
