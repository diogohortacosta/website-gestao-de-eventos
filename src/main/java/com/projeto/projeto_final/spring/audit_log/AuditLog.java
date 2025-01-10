package com.projeto.projeto_final.spring.audit_log;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_log")
public class AuditLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 10L;

    @Transient
    private String formattedTimestamp;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String action; // CREATE, UPDATE, DELETE

    private String user; // Quem fez a ação

    private String entityName;

    private int entityId;

    private LocalDateTime timestamp;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String beforeData; // JSON com o estado anterior

    @Lob
    @Column(columnDefinition = "TEXT")
    private String afterData; // JSON com o estado atual

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", entityName='" + entityName + '\'' +
                ", entityId=" + entityId +
                ", action='" + action + '\'' +
                ", user='" + user + '\'' +
                ", timestamp=" + timestamp +
                ", beforeData='" + beforeData + '\'' +
                ", afterData='" + afterData + '\'' +
                '}';
    }
}
