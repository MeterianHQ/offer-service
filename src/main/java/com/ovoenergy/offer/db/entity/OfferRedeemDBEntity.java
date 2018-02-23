package com.ovoenergy.offer.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "offer_redeem")
@Table(name = "offer_redeem", schema = "offers_db")
@Data
@EqualsAndHashCode(exclude = {"id", "offerRedeemEventDBEntities"})
@ToString(exclude = "offerRedeemEventDBEntities")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferRedeemDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private OfferDBEntity offerDBEntity;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "updated_on", nullable = false)
    private Long updatedOn;

    @Column(name = "link", nullable = false)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LinkStatusType status;

    @OneToMany(mappedBy = "offerRedeemDBEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OfferRedeemEventDBEntity> offerRedeemEventDBEntities = new ArrayList<>();
}
