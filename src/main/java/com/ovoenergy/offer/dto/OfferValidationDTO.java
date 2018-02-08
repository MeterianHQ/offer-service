package com.ovoenergy.offer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiModel(value = "Validation", description = "Validation information about every field that failed to pass internal validation system")
@JsonSerialize
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class OfferValidationDTO extends OfferDTO {

    @ApiModelProperty(name = "constraintViolations", required = true)
    private Map<String, Set<ErrorMessageDTO>> constraintViolations =  new HashMap<>();

    public OfferValidationDTO(OfferDTO offerDTO) {
        super();
        this.setOfferCode(offerDTO.getOfferCode());
        this.setOfferName(offerDTO.getOfferName());
        this.setDescription(offerDTO.getDescription());
        this.setEligibilityCriteria(offerDTO.getEligibilityCriteria());
        this.setExpiryDate(offerDTO.getExpiryDate());
        this.setStartDate(offerDTO.getStartDate());
        this.setIsExpirable(offerDTO.getIsExpirable());
        this.setChannel(offerDTO.getChannel());
        this.setMaxOfferRedemptions(offerDTO.getMaxOfferRedemptions());
        this.setOfferType(offerDTO.getOfferType());
        this.setSupplier(offerDTO.getSupplier());
        this.setValue(offerDTO.getValue());
    }
}
