package com.ovoenergy.offer.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    @ManyToOne
    @JoinColumn(name = "offer_redeem_id", nullable = false)
    private OfferRedeemDBEntity offerRedeemDBEntity;

    @Column(name = "updated_on", nullable = false)
    private Long updatedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LinkStatusType status;
}
