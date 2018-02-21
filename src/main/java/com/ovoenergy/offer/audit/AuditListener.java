package com.ovoenergy.offer.audit;

import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("listener")
public class AuditListener implements PostUpdateEventListener, PostInsertEventListener {

    private final Map<Class<?>, Set<FieldInfo>> classWithFieldInfo;
    private final AuditRepository auditRepository;
    private final JdbcHelper jdbcHelper;

    @Autowired
    public AuditListener(Map<Class<?>, Set<FieldInfo>> classWithFieldInfo, AuditRepository auditRepository, JdbcHelper jdbcHelper) {
        this.classWithFieldInfo = classWithFieldInfo;
        this.auditRepository = auditRepository;
        this.jdbcHelper = jdbcHelper;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (classWithFieldInfo.containsKey(event.getClass())) {
            Set<FieldInfo> fieldInfoSet = classWithFieldInfo.get(event.getClass());
            Object[] state = event.getState();
            List<AuditDBEntity> auditDBEntities = new ArrayList<>();
            for (FieldInfo fieldInfo : fieldInfoSet) {
                Object o = state[fieldInfo.getFieldIndex()];
                AuditDBEntity auditDBEntity = AuditDBEntity.builder()
                        .entityId((Long) event.getId())
                        .entityName(event.getClass().getSimpleName())
                        .entityUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime())
                        .fieldName(fieldInfo.getFieldName())
                        .newValue(Objects.toString(o))
                        .updateOn(null)
                        .build();
                auditDBEntities.add(auditDBEntity);
            }
            auditRepository.save(auditDBEntities);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (classWithFieldInfo.containsKey(event.getClass())) {
            Set<FieldInfo> fieldInfoSet = classWithFieldInfo.get(event.getClass());
            Object[] state = event.getState();
            Object[] oldState = event.getOldState();
            List<AuditDBEntity> auditDBEntities = new ArrayList<>();
            for (FieldInfo fieldInfo : fieldInfoSet) {
                Object newValue = state[fieldInfo.getFieldIndex()];
                Object oldValue = oldState[fieldInfo.getFieldIndex()];
                if (Objects.equals(newValue, oldState)) {
                    continue;
                }
                AuditDBEntity auditDBEntity = AuditDBEntity.builder()
                        .entityId((Long) event.getId())
                        .entityName(event.getClass().getSimpleName())
                        .entityUpdatedOn(jdbcHelper.lookupCurrentDbTime().getTime())
                        .fieldName(fieldInfo.getFieldName())
                        .newValue(Objects.toString(newValue))
                        .oldValue(Objects.toString(oldValue))
                        .updateOn(null)
                        .build();
                auditDBEntities.add(auditDBEntity);
            }
            auditRepository.save(auditDBEntities);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
