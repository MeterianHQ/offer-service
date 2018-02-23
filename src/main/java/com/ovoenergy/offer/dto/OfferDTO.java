package com.ovoenergy.offer.dto;

import com.ovoenergy.offer.db.repository.OfferRepository;
import com.ovoenergy.offer.validation.group.*;
import com.ovoenergy.offer.validation.key.CodeKeys;
import com.ovoenergy.offer.validation.validator.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Offer", description = "Offer information")
@StartDateNotUpdatableConstraint(groups = RequiredActiveOfferChecks.class)
@OfferCodeConstraint(groups = {RequiredActiveOfferChecks.class, RequiredDraftOfferChecks.class})
@ExpiryDateFieldsValueConstraint(groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
@DateFieldsValueConstraint(groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
@Builder
public class OfferDTO {

    @EntityExistsConstraint(groups = RequiredOfferUpdateChecks.class, repository = OfferRepository.class)
    @Null(groups = RequiredOfferCreateChecks.class, message = CodeKeys.NULL_FIELD)
    @NotNull(groups = RequiredOfferUpdateChecks.class, message = CodeKeys.NOT_NULL_FIELD)
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @ApiModelProperty(name = "offerCode", required = true)
    @Size(groups = BaseOfferChecks.class, message = CodeKeys.OFFER_CODE_FIELD_SIZE, max = 100)
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED, groups = BaseOfferChecks.class)
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = CodeKeys.INVALID_OFFER_CODE, groups = BaseOfferChecks.class)
    private String offerCode;

    @ApiModelProperty(name = "offerName", required = true)
    @Size(groups = BaseOfferChecks.class, message = CodeKeys.OFFER_NAME_FIELD_SIZE, max = 50)
    @NotEmpty(message = CodeKeys.FIELD_REQUIRED, groups = RequiredActiveOfferChecks.class)
    @Null(groups = EmptyDraftOfferChecks.class, message = CodeKeys.NULL_FIELD)
    private String offerName;

    @ApiModelProperty(name = "description", required = true)
    @Size(groups = BaseOfferChecks.class, message = CodeKeys.OFFER_DESCRIPTION_FIELD_SIZE, max = 50)
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
    @Null(groups = EmptyDraftOfferChecks.class, message = CodeKeys.NULL_FIELD)
    private Long value;

    @ApiModelProperty(name = "maxOfferRedemptions", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Min(value = 1, message = CodeKeys.INPUT_VALUE_ZERO, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Max(value = 99999999, message = CodeKeys.INPUT_REDEMPTION_MAX, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class, message = CodeKeys.NULL_FIELD)
    private Long maxOfferRedemptions;

    @ApiModelProperty(name = "actualOfferRedemptions", notes = "response field only", required = true)
    private Long actualOfferRedemptions;

    @ApiModelProperty(name = "startDate", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @FutureDateConstraint(groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class, message = CodeKeys.NULL_FIELD)
    private Long startDate;

    @ApiModelProperty(name = "expiryDate", required = true)
    @FutureDateConstraint(groups = {RequiredActiveOfferChecks.class, NonEmptyDraftOfferChecks.class})
    @Null(groups = EmptyDraftOfferChecks.class, message = CodeKeys.NULL_FIELD)
    private Long expiryDate;

    @ApiModelProperty(name = "isExpirable", required = true)
    @NotNull(message = CodeKeys.NOT_NULL_FIELD, groups = BaseOfferChecks.class)
    private Boolean isExpirable;

    @ApiModelProperty(name = "eligibilityCriteria", required = true)
    @Pattern(regexp = "^(?i)(SSD)$", message = CodeKeys.PROVIDED_VALUE_NOT_SUPPORTED, groups = BaseOfferChecks.class)
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
