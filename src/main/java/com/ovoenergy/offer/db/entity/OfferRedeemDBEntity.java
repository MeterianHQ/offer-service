package com.ovoenergy.offer.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity(name = "offer_redeem")
@Table(name = "offer_redeem", schema = "offers_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(OfferRedeemId.class)
public class OfferRedeemDBEntity {
    @Id
    @Column(name = "id_offer", nullable = false)
    private Long idOffer;

    @Id
    @Column(name = "email")
    private String email;

    @Id
    @Column(name = "updated_on")
    private Long updatedOn;
}
