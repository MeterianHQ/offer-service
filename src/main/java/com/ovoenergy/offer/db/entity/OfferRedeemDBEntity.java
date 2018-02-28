package com.ovoenergy.offer.db.entity;

import com.ovoenergy.offer.audit.Auditable;
import com.ovoenergy.offer.audit.AuditableField;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Auditable
@Entity(name = "offer_redeem")
@Table(name = "offer_redeem", schema = "offers_db")
@Data
@EqualsAndHashCode(exclude = {"id", "offerRedeemEventDBEntities", "offerDBEntity"})
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
    @JoinColumn(name = "id_offer", nullable = false)
    private OfferDBEntity offerDBEntity;

    @AuditableField
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "updated_on", nullable = false)
    private Long updatedOn;

    @AuditableField
    @Column(name = "hash")
    private String hash;

    @AuditableField
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferRedeemStatusType status;

    @AuditableField
    @Column(name = "expired_on")
    private Long expiredOn;

    @Builder.Default
    @OneToMany(mappedBy = "offerRedeemDBEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<OfferRedeemEventDBEntity> offerRedeemEventDBEntities = new HashSet<>();

    @PrePersist
    public void onPersist() {
        OfferRedeemEventDBEntity offerRedeemEventDBEntity = OfferRedeemEventDBEntity.builder()
                .offerRedeemDBEntity(this)
                .status(status)
                .updatedOn(updatedOn)
                .build();
        offerRedeemEventDBEntities.add(offerRedeemEventDBEntity);
    }

    @PreUpdate
    public void onUpdate() {
        OfferRedeemEventDBEntity offerRedeemEventDBEntity = OfferRedeemEventDBEntity.builder()
                .offerRedeemDBEntity(this)
                .status(status)
                .updatedOn(updatedOn)
                .build();
        offerRedeemEventDBEntities.add(offerRedeemEventDBEntity);
    }
}
