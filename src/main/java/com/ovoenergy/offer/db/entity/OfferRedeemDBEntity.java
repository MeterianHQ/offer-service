package com.ovoenergy.offer.db.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "offer_redeem")
@Table(name = "offer_redeem", schema = "offers_db")
@Getter
@Setter
@EqualsAndHashCode
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
