package com.ovoenergy.offer.db.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "offer_redeem_event")
@Table(name = "offer_redeem_event", schema = "offers_db")
@Data
@EqualsAndHashCode(exclude = {"id", "offerRedeemDBEntity"})
@ToString(exclude = "offerRedeemDBEntity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferRedeemEventDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "offer_redeem_id", nullable = false)
    private OfferRedeemDBEntity offerRedeemDBEntity;

    @Column(name = "updated_on", nullable = false)
    private Long updatedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferRedeemStatusType status;
}
