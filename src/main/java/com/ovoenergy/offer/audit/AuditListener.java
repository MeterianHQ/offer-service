package com.ovoenergy.offer.audit;

import com.google.common.primitives.Ints;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class AuditListener implements PostUpdateEventListener, PostInsertEventListener {

    private final Map<Class<?>, Set<AuditableFieldInfo>> classWithAuditableFieldInfo;
    private final AuditRepository auditRepository;
    private final JdbcHelper jdbcHelper;

    public AuditListener(Map<Class<?>, Set<AuditableFieldInfo>> classWithAuditableFieldInfo, AuditRepository auditRepository, JdbcHelper jdbcHelper) {
        this.classWithAuditableFieldInfo = classWithAuditableFieldInfo;
        this.auditRepository = auditRepository;
        this.jdbcHelper = jdbcHelper;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (classWithAuditableFieldInfo.containsKey(event.getClass())) {
            Set<AuditableFieldInfo> fieldInfoSet = classWithAuditableFieldInfo.get(event.getClass());
            Object[] state = event.getState();

            List<AuditDBEntity> auditDBEntities = new ArrayList<>();
            for (AuditableFieldInfo auditableFieldInfo : fieldInfoSet) {
                Object o = state[auditableFieldInfo.getFieldIndex()];
                AuditDBEntity auditDBEntity = AuditDBEntity.builder()
                        .entityId((Long) event.getId())
                        .entityName(getEntityName(event.getPersister()))
                        .updateOn(jdbcHelper.lookupCurrentDbTime().getTime())
                        .fieldName(auditableFieldInfo.getFieldName())
                        .newValue(Objects.toString(o))
                        .build();
                auditDBEntities.add(auditDBEntity);
            }

            auditRepository.save(auditDBEntities);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (classWithAuditableFieldInfo.containsKey(event.getClass())) {
            Set<AuditableFieldInfo> fieldInfoSet = classWithAuditableFieldInfo.get(event.getClass());
            Object[] state = event.getState();
            Object[] oldState = event.getOldState();
            List<Integer> dirtyPropertyIndexes = Ints.asList(event.getDirtyProperties());

            List<AuditDBEntity> auditDBEntities = new ArrayList<>();
            for (AuditableFieldInfo auditableFieldInfo : fieldInfoSet) {
                if (dirtyPropertyIndexes.contains(auditableFieldInfo.getFieldIndex())) {
                    Object newValue = state[auditableFieldInfo.getFieldIndex()];
                    Object oldValue = oldState[auditableFieldInfo.getFieldIndex()];
                    AuditDBEntity auditDBEntity = AuditDBEntity.builder()
                            .entityId((Long) event.getId())
                            .entityName(getEntityName(event.getPersister()))
                            .updateOn(jdbcHelper.lookupCurrentDbTime().getTime())
                            .fieldName(auditableFieldInfo.getFieldName())
                            .newValue(Objects.toString(newValue))
                            .oldValue(Objects.toString(oldValue))
                            .build();
                    auditDBEntities.add(auditDBEntity);
                }
            }

            auditRepository.save(auditDBEntities);
        }
    }

    private String getEntityName(EntityPersister persister) {
        return ((AbstractEntityPersister) persister).getTableName();
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
