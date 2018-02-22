package com.ovoenergy.offer.db.repository;

import com.ovoenergy.offer.db.entity.AuditDBEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditDBEntity, Long> {
}
