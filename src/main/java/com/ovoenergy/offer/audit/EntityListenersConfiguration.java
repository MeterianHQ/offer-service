package com.ovoenergy.offer.audit;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class EntityListenersConfiguration {

    @Autowired
    private HibernateEntityManagerFactory hibernateEntityManagerFactory;

    @Autowired
    private AuditListener auditListener;

    @PostConstruct
    public void registerListeners() {
        EventListenerRegistry listenerRegistry = hibernateEntityManagerFactory.getSessionFactory()
                .getServiceRegistry().getService(EventListenerRegistry.class);
        listenerRegistry.appendListeners(EventType.POST_INSERT, auditListener);
        listenerRegistry.appendListeners(EventType.POST_UPDATE, auditListener);
    }

    @Bean
    public Set<Class<?>> auditClasses() {
        final Reflections reflections = new Reflections(
                "com.ovoenergy.offer",
                new TypeAnnotationsScanner(),
                new SubTypesScanner(true)
        );

        return reflections.getTypesAnnotatedWith(Auditable.class);
    }
}
