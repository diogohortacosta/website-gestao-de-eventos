package com.projeto.projeto_final.spring.rest_controller;

import com.projeto.projeto_final.spring.audit_log.AuditLog;
import com.projeto.projeto_final.spring.audit_log.AuditLogRepository;
import com.projeto.projeto_final.spring.chat.ChatMessageDTO;
import com.projeto.projeto_final.spring.chat.ChatMessageService;
import com.projeto.projeto_final.spring.event.EventDTO;
import com.projeto.projeto_final.spring.event.EventIDs;
import com.projeto.projeto_final.spring.task.TaskDTO;
import com.projeto.projeto_final.spring.event.EventService;
import com.projeto.projeto_final.spring.task.TaskService;
import com.projeto.projeto_final.spring.user.UserDTO;
import com.projeto.projeto_final.spring.user.UserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
public class RestController {
    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping("/events/all")
    public List<EventDTO> getAllActiveEvents() {
        return eventService.findAllActiveEvents();
    }

    @GetMapping("/events/personal")
    public List<EventDTO> getPersonalActiveEvents(Principal principal) {
        return eventService.findActiveEventsUsername(principal.getName());
    }

    @GetMapping("/tasks/{taskid}")
    public TaskDTO getTask(@PathVariable("taskid") int id) {
        try {
            return taskService.convertEntityToDto(taskService.findById(id));
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/users")
    public List<UserDTO> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public List<ChatMessageDTO> findChatMessages(@PathVariable("senderId") String senderId, @PathVariable("recipientId") String recipientId) {
        return chatMessageService.findChatMessages(senderId, recipientId);
    }

    @GetMapping("/get-event-auditlogs/{eventid}")
    public Page<AuditLog> getAuditLogsByEventIDs(
            @PathVariable("eventid") int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        EventIDs eventIDs = eventService.getRelatedEventIDs(id);

        // Tem ordenação por timestamp, do mais novo para o mais antigo
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Specification<AuditLog> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Condição para o evento principal
            predicates.add(criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("entityName"), "Event"),
                    criteriaBuilder.equal(root.get("entityId"), eventIDs.getEventId())
            ));

            // Condição para as tasks
            if (eventIDs.getTaskIds() != null && !eventIDs.getTaskIds().isEmpty()) {
                predicates.add(criteriaBuilder.and(
                        root.get("entityId").in(eventIDs.getTaskIds()),
                        criteriaBuilder.equal(root.get("entityName"), "Task")
                ));
            }

            // Condição para os boards
            if (eventIDs.getBoardIds() != null && !eventIDs.getBoardIds().isEmpty()) {
                predicates.add(criteriaBuilder.and(
                        root.get("entityId").in(eventIDs.getBoardIds()),
                        criteriaBuilder.equal(root.get("entityName"), "Board")
                ));
            }

            // Condição para as activities
            if (eventIDs.getActivityIds() != null && !eventIDs.getActivityIds().isEmpty()) {
                predicates.add(criteriaBuilder.and(
                        root.get("entityId").in(eventIDs.getActivityIds()),
                        criteriaBuilder.equal(root.get("entityName"), "Activity")
                ));
            }

            // Condição para as subActivities
            if (eventIDs.getSubActivityIds() != null && !eventIDs.getSubActivityIds().isEmpty()) {
                predicates.add(criteriaBuilder.and(
                        root.get("entityId").in(eventIDs.getSubActivityIds()),
                        criteriaBuilder.equal(root.get("entityName"), "SubActivity")
                ));
            }

            // Combina todas as condições com OR
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(specification, pageable);
    }
}
