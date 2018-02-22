package com.ovoenergy.offer.audit;

import com.google.common.primitives.Ints;
import com.ovoenergy.offer.db.entity.AuditDBEntity;
import com.ovoenergy.offer.db.jdbc.JdbcHelper;
import com.ovoenergy.offer.db.repository.AuditRepository;
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

    private final Map<Class<?>, Set<String>> classWithAuditableFieldNames;
    private final AuditRepository auditRepository;
    private final JdbcHelper jdbcHelper;

    public AuditListener(Map<Class<?>, Set<String>> classWithAuditableFieldNames, AuditRepository auditRepository, JdbcHelper jdbcHelper) {
        this.classWithAuditableFieldNames = classWithAuditableFieldNames;
        this.auditRepository = auditRepository;
        this.jdbcHelper = jdbcHelper;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (classWithAuditableFieldNames.containsKey(event.getEntity().getClass())) {
            Set<String> fieldNames = classWithAuditableFieldNames.get(event.getEntity().getClass());
            Object[] state = event.getState();

            List<AuditDBEntity> auditDBEntities = new ArrayList<>();
            for (String fieldName: fieldNames) {
                int index = getIndexByFieldName(event.getPersister(), fieldName);
                Object o = state[index];
                AuditDBEntity auditDBEntity = AuditDBEntity.builder()
                        .entityId((Long) event.getId())
                        .entityName(getEntityName(event.getPersister()))
                        .updatedOn(jdbcHelper.lookupCurrentDbTime().getTime())
                        .fieldName(fieldName)
                        .newValue(Objects.toString(o))
                        .build();
                auditDBEntities.add(auditDBEntity);
            }

            auditRepository.save(auditDBEntities);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (classWithAuditableFieldNames.containsKey(event.getEntity().getClass())) {
            Set<String> fieldNames = classWithAuditableFieldNames.get(event.getEntity().getClass());
            Object[] state = event.getState();
            Object[] oldState = event.getOldState();
            List<Integer> dirtyPropertyIndexes = Ints.asList(event.getDirtyProperties());

            List<AuditDBEntity> auditDBEntities = new ArrayList<>();
            for (String fieldName: fieldNames) {
                int index = getIndexByFieldName(event.getPersister(), fieldName);
                if (dirtyPropertyIndexes.contains(index)) {
                    Object newValue = state[index];
                    Object oldValue = oldState[index];
                    AuditDBEntity auditDBEntity = AuditDBEntity.builder()
                            .entityId((Long) event.getId())
                            .entityName(getEntityName(event.getPersister()))
                            .updatedOn(jdbcHelper.lookupCurrentDbTime().getTime())
                            .fieldName(fieldName)
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

    private int getIndexByFieldName(EntityPersister persister, String fieldName) {
        return ((AbstractEntityPersister) persister).getPropertyIndex(fieldName);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return true;
    }
}
