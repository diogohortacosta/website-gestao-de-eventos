package com.projeto.projeto_final.spring.audit_log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projeto.projeto_final.spring.event.Event;
import com.projeto.projeto_final.spring.event.EventDTOLog;
import com.projeto.projeto_final.spring.task.Task;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class AuditListener {

    private static AuditLogRepository auditLogRepository;

    @Autowired
    public void setEntityManager (AuditLogRepository auditLogRepository) {
        AuditListener.auditLogRepository = auditLogRepository;
    }

    private static final Logger logger = Logger.getLogger(AuditListener.class.getName());

    @PostPersist
    public void beforeCreate(Object entity) {
        logAction("CREATE", null, entity);
    }

    @PreUpdate
    public void beforeUpdate(Object entity) {
        try {
            Object oldState = getOldState(entity);

            if (entity instanceof Task) {
                Object skipPreUpdateValue = getSkipPreUpdate(entity);

                if (skipPreUpdateValue instanceof Boolean && (Boolean) skipPreUpdateValue) {
                    return;
                } else if (skipPreUpdateValue instanceof Boolean) {
                    if (isDeletionStatusChangedToDeleted(oldState, entity)) {
                        logAction("DELETE", entity, null);
                    } else {
                        logAction("UPDATE", oldState, entity);
                    }
                }
            } else {
                if (isDeletionStatusChangedToDeleted(oldState, entity)) {
                    logAction("DELETE", entity, null);
                } else {
                    logAction("UPDATE", oldState, entity);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating entity", e);
        }
    }

    @PreRemove
    public void beforeDelete(Object entity) {
        logAction("DELETE", entity, null);
    }

    private void logAction(String action, Object oldState, Object newState) {
        String user;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            user = authentication.getName();
        } else {
            user = "${Server}";
        }

        AuditLog log = new AuditLog();

        if (oldState != null) {
            log.setEntityName(oldState.getClass().getSimpleName());
            log.setEntityId((int) getId(oldState));
        } else if (newState != null) {
            log.setEntityName(newState.getClass().getSimpleName());
            log.setEntityId((int) getId(newState));
        }

        log.setAction(action);
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now());

        if ((oldState instanceof Event && newState instanceof Event) || (oldState instanceof Event && newState == null) || (oldState == null && newState instanceof Event)) {
            if (action.equals("DELETE")) {
                log.setBeforeData(oldState != null ? convertObjectDTOToJson(getOldEventDTO(oldState, getOldEditors(oldState))) : null);
            } else {
                log.setBeforeData(oldState != null ? convertObjectDTOToJson(getOldEventDTO(oldState, getOldEditors(newState))) : null);
            }
            log.setAfterData(newState != null ? convertObjectDTOToJson(getNewEventDTO(newState)) : null);
        } else {
            log.setBeforeData(oldState != null ? convertObjectDTOToJson(getEntityDTO(oldState)) : null);
            log.setAfterData(newState != null ? convertObjectDTOToJson(getEntityDTO(newState)) : null);
        }

        auditLogRepository.save(log);
    }

    private boolean isDeletionStatusChangedToDeleted(Object oldState, Object newState) {
        return getDeletionStatus(oldState) != getDeletionStatus(newState);
    }

    public static String convertObjectDTOToJson(Object entity) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Error converting to JSON", e);
            return null;
        }
    }

    private Object getId(Object entity) {
        try {
            return entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting entity ID", e);
            return null;
        }
    }

    private Object getOldState(Object entity) {
        try {
            return entity.getClass().getMethod("getOldState").invoke(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting entity OldState", e);
            return null;
        }
    }

    private List<String> getOldEditors(Object entity) {
        try {
            Object result = entity.getClass().getMethod("getOldEditors").invoke(entity);

            if (result instanceof List<?>) {
                // Verifica se todos os itens são strings e faz o cast
                return ((List<?>) result).stream()
                        .filter(item -> item instanceof String)  // Filtra apenas os itens que são Strings
                        .map(item -> (String) item)  // Faz o cast para String
                        .collect(Collectors.toList());
            } else {
                logger.log(Level.WARNING, "Returned object is not a List");
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting entity OldState", e);
            return null;
        }
    }

    private Object getDeletionStatus(Object entity) {
        try {
            return entity.getClass().getMethod("getDeletionStatus").invoke(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting entity DeletionStatus", e);
            return null;
        }
    }

    private Object getSkipPreUpdate(Object entity) {
        try {
            return entity.getClass().getMethod("isSkipPreUpdate").invoke(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting entity SkipPreUpdate", e);
            return null;
        }
    }

    private Object getEntityDTO(Object entity) {
        try {
            return entity.getClass().getMethod("convertToDTO", entity.getClass()).invoke(entity, entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting entity DTO", e);
            return null;
        }
    }

    private Object getOldEventDTO(Object event, List<String> editorsList) {
        try {
            return event.getClass()
                    .getMethod("convertToDTO", Event.class, List.class)
                    .invoke(event, (Event) event, editorsList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting event DTO", e);
            return null;
        }
    }

    private Object getNewEventDTO(Object event) {
        try {
            return event.getClass().getMethod("convertToDTO", event.getClass()).invoke(event, event);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting event DTO", e);
            return null;
        }
    }
}
