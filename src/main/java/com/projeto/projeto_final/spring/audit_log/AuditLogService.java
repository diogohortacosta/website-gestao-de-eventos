package com.projeto.projeto_final.spring.audit_log;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    public Page<AuditLog> searchAuditLogs(int page, int size, String username, String entityName, Integer entityId, String action, LocalDateTime startDate, LocalDateTime endDate, Sort.Direction sortDirection) {
        return auditLogRepository.findAll(createSpecification(username, entityName, entityId, action, startDate, endDate), PageRequest.of(page, size, sortDirection, "timestamp"));
    }

    private Specification<AuditLog> createSpecification(String username, String entityName, Integer entityId, String action, LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction(); // Inicializa um predicate vazio

            if (username != null && !username.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("user"), username));
            }
            if (entityName != null && !entityName.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("entityName"), entityName));
            }
            if (entityId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("entityId"), entityId));
            }
            if (action != null && !action.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("action"), action));
            }
            if (startDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }
            if (endDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }
            return predicate;
        };
    }

    public long getTotalLogsCount() {
        return auditLogRepository.count();
    }

    public int getTotalPages(int size) {
        long totalLogs = getTotalLogsCount();
        return (int) Math.ceil((double) totalLogs / size);
    }
}
