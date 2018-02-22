package com.ovoenergy.offer.audit;

import com.ovoenergy.offer.db.jdbc.JdbcHelper;
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

    @Bean("listener")
    @Autowired
    public AuditListener auditListener(Map<Class<?>, Set<AuditableFieldInfo>> classWithAuditableFieldInfo,
                                       AuditRepository auditRepository, JdbcHelper jdbcHelper) {
        return new AuditListener(classWithAuditableFieldInfo, auditRepository, jdbcHelper);
    }

    @Bean
    public Map<Class<?>, Set<AuditableFieldInfo>> classWithAuditableFieldInfo() {
        final Reflections reflections = new Reflections(
                "com.ovoenergy.offer",
                new TypeAnnotationsScanner(),
                new SubTypesScanner(true)
        );

        Map<Class<?>, Set<AuditableFieldInfo>> classWithAuditableFieldInfo = new HashMap<>();
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Auditable.class);
        for (Class<?> clazz : classes) {
            List<Field> allFieldsList = FieldUtils.getAllFieldsList(clazz);
            List<Field> annotatedFields = FieldUtils.getFieldsListWithAnnotation(clazz, AuditableField.class);
            Set<AuditableFieldInfo> indexes = annotatedFields.stream()
                    .map(field -> new AuditableFieldInfo(allFieldsList.indexOf(field), field.getName()))
                    .collect(Collectors.toSet());
            classWithAuditableFieldInfo.put(clazz, indexes);
        }
        return classWithAuditableFieldInfo;
    }
}
