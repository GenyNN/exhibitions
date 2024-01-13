package org.fally.einvite.repository;

import org.fally.einvite.model.AuditLog;
import org.fally.einvite.model.AwardIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, AwardIdentity> {
}
