package com.ovoenergy.offer.dto  ;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ovoenergy.offer.validation.group.*;
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

import javax.validation.constraints.*;

@Data
@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Offer", description = "Offer information")
@ExpiryDateFieldsValueConstraint(message = CodeKeys.NO_EXPIRY_OFFER_COULD_NOT_HAVE_EXPIRY_DATE, propertyPath = "expiryDate", groups = {RequiredActiveOfferChecks.class, RequiredDraftOfferChecks.class})
@DateFieldsValueConstraint(message = CodeKeys.OFFER_EXPIRY_DATE_BEFORE_START_DATE, propertyPath = "expiryDate", groups = {RequiredActiveOfferChecks.class, RequiredDraftOfferChecks.class})
@Builder
public class OfferDTO {

    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @ApiModelProperty(name = "offerCode", required = true)
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED, groups = BaseOfferChecks.class)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD , groups = BaseOfferChecks.class)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = CodeKeys.INVALID_OFFER_CODE, groups = BaseOfferChecks.class)
    private String offerCode;

    @ApiModelProperty(name = "offerName", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED, groups = {RequiredActiveOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class)
    private String offerName;

    @ApiModelProperty(name = "description", required = true)
    private String description;

    @ApiModelProperty(name = "supplier", required = true)
    @Pattern(regexp = "^(?i)(Amazon)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED, groups = BaseOfferChecks.class)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = BaseOfferChecks.class)
    private String supplier;

    @ApiModelProperty(name = "offerType", required = true)
    @Pattern(regexp = "^(?i)(Giftcard)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED, groups = BaseOfferChecks.class)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = BaseOfferChecks.class)
    private String offerType;

    @ApiModelProperty(name = "value", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Min(value = 1, message = CodeKeys.INPUT_VALUE_ZERO, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Max(value = 999, message = CodeKeys.INPUT_VALUE_MAX, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class)
    private Long value;

    @ApiModelProperty(name = "maxOfferRedemptions", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Min(value = 1, message = CodeKeys.INPUT_VALUE_ZERO, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Max(value = 99999999, message = CodeKeys.INPUT_REDEMPTION_MAX, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class)
    private Long maxOfferRedemptions;

    @ApiModelProperty(name = "actualOfferRedemptions", notes = "response field only", required = true)
    private Long actualOfferRedemptions;

    @ApiModelProperty(name = "startDate", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @FutureDateConstraint(message = CodeKeys.NON_IN_FUTURE_DATE, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class)
    private Long startDate;

    @ApiModelProperty(name = "expiryDate", required = true)
    @FutureDateConstraint(message = CodeKeys.NON_IN_FUTURE_DATE, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class)
    private Long expiryDate;

    @ApiModelProperty(name = "isExpirable", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = BaseOfferChecks.class)
    private Boolean isExpirable;

    @ApiModelProperty(name = "eligibilityCriteria", required = true)
    @Pattern(regexp = "^(?i)(SSD)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED,groups = BaseOfferChecks.class)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = BaseOfferChecks.class)
    private String eligibilityCriteria;

    @ApiModelProperty(name = "channel", required = true)
    @Pattern(regexp = "^(?i)(Email|Display|Social)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED, groups = BaseOfferChecks.class)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = BaseOfferChecks.class)
    private String channel;

    @ApiModelProperty(name = "status", required = true)
    @Pattern(regexp = "^(?i)(Active|Draft)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED, groups = BaseOfferChecks.class)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = BaseOfferChecks.class)
    private String status;

    @ApiModelProperty(name = "updatedOn", notes = "response field only", required = true)
    private Long updatedOn;
}
