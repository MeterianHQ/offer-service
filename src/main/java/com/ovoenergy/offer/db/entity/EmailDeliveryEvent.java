package com.ovoenergy.offer.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "email_delivery_event")
@Table(name = "email_delivery_event", schema = "offers_db")
@Data
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDeliveryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "trace_token")
    private String traceToken;

    @Column(name = "deliver_to")
    private String deliverOn;

    @Column(name = "updated_on")
    private Long updatedOn;

    @Column(name = "source")
    private String source;

    @Column(name = "trigger_source")
    private String triggerSource;

    @Builder.Default
    @OneToMany(mappedBy = "emailDeliveryEvent", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<EmailDeliveryEventStatus> emailDeliveryEventStatuses = new HashSet<>();

    @PrePersist
    public void onPersist() {
        EmailDeliveryEventStatus emailDeliveryEventStatus = EmailDeliveryEventStatus.builder()
                .emailDeliveryEvent(this)
                .status(null)
                .updatedOn(updatedOn)
                .build();
        emailDeliveryEventStatuses.add(emailDeliveryEventStatus);
    }

    @PreUpdate
    public void onUpdate() {
        EmailDeliveryEventStatus emailDeliveryEventStatus = EmailDeliveryEventStatus.builder()
                .emailDeliveryEvent(this)
                .status(null)
                .updatedOn(updatedOn)
                .build();
        emailDeliveryEventStatuses.add(emailDeliveryEventStatus);
    }

}
