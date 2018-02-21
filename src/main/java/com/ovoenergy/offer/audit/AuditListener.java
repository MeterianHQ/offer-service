package com.ovoenergy.offer.audit;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class AuditListener implements PostUpdateEventListener, PostInsertEventListener {

    private final Set<Class<?>> auditClasses;
    private final AuditRepository auditRepository;

    @Autowired
    public AuditListener(Set<Class<?>> auditClasses, AuditRepository auditRepository) {
        this.auditClasses = auditClasses;
        this.auditRepository = auditRepository;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        log.info(event.toString());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        log.info(event.toString());
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
