package com.ovoenergy.offer.audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditDBEntity, Long> {
}
