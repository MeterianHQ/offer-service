package com.ovoenergy.offer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.validator.DateFieldsValueConstraint;
import com.ovoenergy.offer.validation.validator.ExpiryDateFieldsValueConstraint;
import com.ovoenergy.offer.validation.validator.FutureDateConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel(value = "Verify Offer Request", description = "Input request for offer verify")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
public class OfferVerifyDTO {

    @ApiModelProperty(name = "offerCode", required = true)
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = CodeKeys.INVALID_OFFER_CODE)
    private String offerCode;
}
