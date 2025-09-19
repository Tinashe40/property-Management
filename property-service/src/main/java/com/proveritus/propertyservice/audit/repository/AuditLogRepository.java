package com.proveritus.propertyservice.audit.repository;

import com.proveritus.propertyservice.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
