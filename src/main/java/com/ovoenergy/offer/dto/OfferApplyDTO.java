package com.ovoenergy.offer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ovoenergy.offer.validation.key.CodeKeys;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel(value = "Apply to Offer Request", description = "Input request to apply to an offer")
@Data
@AllArgsConstructor
@JsonSerialize
@NoArgsConstructor
public class OfferApplyDTO {

    @ApiModelProperty(name = "offerCode", required = true)
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = CodeKeys.INVALID_OFFER_CODE)
    private String offerCode;

    @ApiModelProperty(name = "email", required = true)
    @Email(message = CodeKeys.INVALID_EMAIL)
    private String email;

}
