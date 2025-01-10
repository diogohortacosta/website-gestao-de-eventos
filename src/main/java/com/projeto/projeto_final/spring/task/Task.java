package com.projeto.projeto_final.spring.task;

import com.projeto.projeto_final.spring.activity.Activity;
import com.projeto.projeto_final.spring.activity.ActivityDTO;
import com.projeto.projeto_final.spring.audit_log.AuditListener;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import com.projeto.projeto_final.spring.event.Event;
import com.projeto.projeto_final.spring.utils.EntityUtils;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditListener.class)
@Table(name="tasks")
public class Task implements Serializable {

    @Serial
    private static final long serialVersionUID = 4L;

    @Transient
    private Task oldState;

    @Transient
    private boolean skipPreUpdate = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private DeletionStatus deletionStatus = DeletionStatus.ACTIVE;

    @Column(nullable=false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private int position;

    public Task(int id, String title, LocalDateTime startDate, LocalDateTime endDate, Event event, int position) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.event = event;
        this.position = position;
    }

    public void setOldState(Task oldState) {
        this.oldState = EntityUtils.clone(oldState);
    }

    public void setStartDate(String startDate, String startHour) {
        this.startDate = parseDateTime(startDate, startHour);
    }

    public void setEndDate(String endDate, String endHour) {
        this.endDate = parseDateTime(endDate, endHour);
    }

    private LocalDateTime parseDateTime(String date, String time) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String dateTime1 = date + "T" + time;
        String dateTime2 = reverseDate(date) + "T" + time;

        try {
            return LocalDateTime.parse(dateTime1, formatter1);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(dateTime2, formatter1);
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd or dd-MM-yyyy for dates and HH:mm for time.");
            }
        }
    }

    private String reverseDate(String date) {
        if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            String[] parts = date.split("-");
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        }
        return date;
    }

    public TaskDTO convertToDTO(Task task) {
        TaskDTO taskDto = new TaskDTO();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDates(task.getStartDate(), task.getEndDate());
        taskDto.setEventId(task.getEvent().getId());
        taskDto.setPosition(task.getPosition());

        return taskDto;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", deletionStatus=" + deletionStatus +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", eventId=" + event.getId() +
                ", position=" + position +
                '}';
    }
}
