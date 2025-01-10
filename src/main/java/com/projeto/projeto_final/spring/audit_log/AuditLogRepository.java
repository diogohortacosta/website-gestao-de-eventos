package com.projeto.projeto_final.spring.audit_log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer>, JpaSpecificationExecutor<AuditLog> {

}
