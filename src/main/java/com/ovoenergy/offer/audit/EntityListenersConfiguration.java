package com.ovoenergy.offer.audit;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityListenersConfiguration {

    @Autowired
    private HibernateEntityManagerFactory hibernateEntityManagerFactory;

    @Lazy
    @Qualifier("listener")
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
    public Map<Class<?>, Set<FieldInfo>> auditClasses() {
        final Reflections reflections = new Reflections(
                "com.ovoenergy.offer",
                new TypeAnnotationsScanner(),
                new SubTypesScanner(true)
        );

        Map<Class<?>, Set<FieldInfo>> classWithFieldInfo = new HashMap<>();
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Auditable.class);
        for (Class<?> clazz : classes) {
            List<Field> allFieldsList = FieldUtils.getAllFieldsList(clazz);
            List<Field> annotatedFields = FieldUtils.getFieldsListWithAnnotation(clazz, AuditableField.class);
            Set<FieldInfo> indexes = annotatedFields.stream()
                    .map(field ->new FieldInfo(allFieldsList.indexOf(field), field.getName()))
                    .collect(Collectors.toSet());
            classWithFieldInfo.put(clazz, indexes);
        }
        return classWithFieldInfo;
    }
}
