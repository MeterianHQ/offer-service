package com.ovoenergy.offer.db.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class OfferRedeemId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idOffer;

    private Long updatedOn;

    private String email;
}
