package com.ovoenergy.offer.db.entity;

import com.ovoenergy.offer.audit.Auditable;
import com.ovoenergy.offer.audit.AuditableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Auditable
@Entity(name = "offer")
@Table(name = "offer", schema = "offers_db")
@Data
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @AuditableField
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @AuditableField
    @Column(name = "offer_code")
    private String offerCode;

    @AuditableField
    @Column(name = "offer_name")
    private String offerName;

    @AuditableField
    @Column(name = "description")
    private String description;

    @AuditableField
    @Column(name = "supplier")
    @Enumerated(EnumType.STRING)
    private SupplierType supplier;

    @AuditableField
    @Column(name = "offer_type")
    @Enumerated(EnumType.STRING)
    private OfferType offerType;

    @AuditableField
    @Column(name = "value")
    private Long value;

    @AuditableField
    @Column(name = "max_offer_redemptions")
    private Long maxOfferRedemptions;

    @AuditableField
    @Column(name = "actual_offer_redemptions")
    private Long actualOfferRedemptions;

    @AuditableField
    @Column(name = "start_date")
    private Long startDate;

    @AuditableField
    @Column(name = "expiry_date")
    private Long expiryDate;

    @AuditableField
    @Column(name = "is_expirable")
    private Boolean isExpirable;

    @AuditableField
    @Column(name = "eligibility_criteria")
    @Enumerated(EnumType.STRING)
    private EligibilityCriteriaType eligibilityCriteria;

    @AuditableField
    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private ChannelType channel;

    @Column(name = "updated_on")
    private Long updatedOn;
}
