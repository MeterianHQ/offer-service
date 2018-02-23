package com.ovoenergy.offer.dto;

import com.ovoenergy.offer.validation.key.CodeKeys;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel(value = "Apply to Offer Request", description = "Input request to apply to an offer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferApplyDTO {

    @ApiModelProperty(name = "offerCode", required = true)
    @NotEmpty(message = CodeKeys.OFFER_INVALID)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = CodeKeys.OFFER_INVALID)
    private String offerCode;

    @ApiModelProperty(name = "email", required = true)
    @NotEmpty(message = CodeKeys.INVALID_EMAIL)
    @NotNull(message = CodeKeys.INVALID_EMAIL)
    @Email(message = CodeKeys.INVALID_EMAIL)
    private String email;

    @ApiModelProperty(name = "updatedOn", notes = "response field only", required = true)
    private Long updatedOn;

}
