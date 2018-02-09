package com.ovoenergy.offer.db.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class OfferRedeemId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idOffer;

	private Long updatedOn;

	private String email;
}
