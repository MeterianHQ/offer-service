package com.ovoenergy.offer.db.entity;

import java.io.Serializable;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class OfferRedeemId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idOffer;

	private Long updatedOn;

	private String email;
}
