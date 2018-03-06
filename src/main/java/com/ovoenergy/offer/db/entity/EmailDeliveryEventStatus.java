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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name = "email_delivery_event_status")
@Table(name = "email_delivery_event_status", schema = "offers_db")
@Data
@EqualsAndHashCode(exclude = {"id", "emailDeliveryEvent"})
@ToString(exclude = "emailDeliveryEvent")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDeliveryEventStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "email_delivery_event_id", nullable = false, referencedColumnName = "id")
    private EmailDeliveryEvent emailDeliveryEvent;

    @Column(name = "updated_on", nullable = false)
    private Long updatedOn;

    @Column(name = "status", nullable = false)
    private String status;
}
