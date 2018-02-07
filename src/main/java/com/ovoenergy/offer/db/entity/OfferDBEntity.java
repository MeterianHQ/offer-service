package com.ovoenergy.offer.db.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "offer")
@Table(name = "offer", schema = "offers_db")
@Getter
@Setter
@EqualsAndHashCode(exclude={"id"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferDBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusType status;

    @Column(name = "offer_code")
    private String offerCode;

    @Column(name = "offer_name")
    private String offerName;

    @Column(name = "description")
    private String description;

    @Column(name = "supplier")
    @Enumerated(EnumType.STRING)
    private SupplierType supplier;

    @Column(name = "offer_type")
    @Enumerated(EnumType.STRING)
    private OfferType offerType;

    @Column(name = "value")
    private Long value;

    @Column(name = "max_offer_redemptions")
    private Long maxOfferRedemptions;

    @Column(name = "start_date")
    private Long startDate;

    @Column(name = "expiry_date")
    private Long expiryDate;

    @Column(name = "is_expirable")
    private Boolean isExpirable;

    @Column(name = "eligibility_criteria")
    @Enumerated(EnumType.STRING)
    private EligibilityCriteriaType eligibilityCriteria;

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    private ChannelType channel;

    @Column(name = "updated_on")
    private Long updatedOn;
}
