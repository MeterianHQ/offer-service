package com.ovoenergy.offer.dto  ;

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

@Data
@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Offer", description = "Offer information")
@ExpiryDateFieldsValueConstraint(message = CodeKeys.NO_EXPIRY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE, propertyPath = "expiryDate")
@DateFieldsValueConstraint(message = CodeKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE, propertyPath = "expiryDate")
@Builder
public class OfferDTO {

    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @ApiModelProperty(name = "offerCode", required = true)
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = CodeKeys.INVALID_OFFER_CODE)
    private String offerCode;

    @ApiModelProperty(name = "offerName", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED)
    private String offerName;

    @ApiModelProperty(name = "description", required = true)
    private String description;

    @ApiModelProperty(name = "supplier", required = true)
    @Pattern(regexp = "^(Amazon)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    private String supplier;

    @ApiModelProperty(name = "offerType", required = true)
    @Pattern(regexp = "^(Giftcard)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    private String offerType;

    @ApiModelProperty(name = "value", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    @Min(value = 1, message = CodeKeys.INPUT_VALUE_ZERO)
    @Max(value = 999, message = CodeKeys.INPUT_VALUE_MAX)
    private Long value;

    @ApiModelProperty(name = "maxOfferRedemptions", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    @Min(value = 1, message = CodeKeys.INPUT_VALUE_ZERO)
    @Max(value = 99999999, message = CodeKeys.INPUT_REDEMPTION_MAX)
    private Long maxOfferRedemptions;

    @ApiModelProperty(name = "actualOfferRedemptions", notes = "response field only", required = true)
    private Long actualOfferRedemptions;

    @ApiModelProperty(name = "startDate", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    @FutureDateConstraint(message = CodeKeys.NON_IN_FUTURE_DATE)
    private Long startDate;

    @ApiModelProperty(name = "expiryDate", required = true)
    @FutureDateConstraint(message = CodeKeys.NON_IN_FUTURE_DATE)
    private Long expiryDate;

    @ApiModelProperty(name = "isExpirable", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    private Boolean isExpirable;

    @ApiModelProperty(name = "eligibilityCriteria", required = true)
    @Pattern(regexp = "^(SSD)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    private String eligibilityCriteria;

    @ApiModelProperty(name = "channel", required = true)
    @Pattern(regexp = "^(Email|Display|Social)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD)
    private String channel;

    @ApiModelProperty(name = "actualOfferRedemptions", notes = "response field only", required = true)
    private String status;

    @ApiModelProperty(name = "updatedOn", notes = "response field only", required = true)
    private Long updatedOn;
}
