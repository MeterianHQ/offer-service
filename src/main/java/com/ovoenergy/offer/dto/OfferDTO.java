package com.ovoenergy.offer.dto  ;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonSerialize
@ApiModel(value = "Offer", description = "Offer information")
public class OfferDTO {

    @ApiModelProperty(name = "offerCode", required = false)
    private String offerCode;

    @ApiModelProperty(name = "offerName", required = false)
    private String offerName;

    @ApiModelProperty(name = "description", required = false)
    private String description;

    @ApiModelProperty(name = "supplier", required = false)
    private String supplier;

    @ApiModelProperty(name = "offerType", required = false)
    private String offerType;

    @ApiModelProperty(name = "value", required = false)
    private Long value;

    @ApiModelProperty(name = "maxOfferRedemptions", required = false)
    private Long maxOfferRedemptions;

    @ApiModelProperty(name = "startDate", required = false)
    private Long startDate;

    @ApiModelProperty(name = "expiryDate", required = false)
    private Long expiryDate;

    @ApiModelProperty(name = "isExpirable", required = false)
    private Boolean isExpirable;

    @ApiModelProperty(name = "eligibilityCriteria", required = false)
    private String eligibilityCriteria;

    @ApiModelProperty(name = "channel", required = false)
    private String channel;
}
