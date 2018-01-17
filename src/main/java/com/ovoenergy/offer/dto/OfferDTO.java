package com.ovoenergy.offer.dto  ;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class OfferDTO {

    private String offerCode;

    private String offerName;

    private String description;

    private String supplier;

    private String offerType;

    private Long value;

    private Long maxOfferRedemptions;

    private Long startDate;

    private Long expiryDate;

    private Boolean isExpired;

    private String eligibilityCriteria;

    private String channel;
}
