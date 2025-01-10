package com.projeto.projeto_final.spring.audit_log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/logs")
    public String getAuditLogs(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false) String entityName,
                               @RequestParam(required = false) Integer entityId,
                               @RequestParam(required = false) String action,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               @RequestParam(defaultValue = "DESC") String sortDirection,
                               Model model
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;

        LocalDateTime startDateTime = convertToDateTime(startDate);
        LocalDateTime endDateTime = convertToDateTime(endDate);

        Page<AuditLog> logs = auditLogService.searchAuditLogs(page, size, username, entityName, entityId, action, startDateTime, endDateTime, direction);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (AuditLog log : logs.getContent()) {
            log.setFormattedTimestamp(log.getTimestamp().format(formatter));
        }

        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("auditLogs", logs.getContent());
        model.addAttribute("totalPages", logs.getTotalPages());
        model.addAttribute("totalLogs", logs.getTotalElements());

        model.addAttribute("username", username);
        model.addAttribute("entityName", entityName);
        model.addAttribute("entityId", entityId);
        model.addAttribute("action", action);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortDirection", sortDirection);

        return "log";
    }

    private LocalDateTime convertToDateTime(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null; // Retorne nulo se a string for vazia
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Converte para LocalDate e depois para LocalDateTime (meia-noite do dia)
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return localDate.atStartOfDay(); // Converte LocalDate para LocalDateTime à meia-noite
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + dateString);
            return null;
        }
    }
}
