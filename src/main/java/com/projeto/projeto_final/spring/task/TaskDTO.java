package com.projeto.projeto_final.spring.task;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private int id;
    private String title;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private int eventId;
    private int position;
    private int beforeColspan = 0;
    private int colspan = 0;

    public void setStartDateTime(LocalDateTime start) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.startDate = start.format(dateFormatter);
        this.startTime = start.format(timeFormatter);
    }

    public void setEndDateTime(LocalDateTime end) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.endDate = end.format(dateFormatter);
        this.endTime = end.format(timeFormatter);
    }

    public void setDates(LocalDateTime start, LocalDateTime end) {
        setStartDateTime(start);
        setEndDateTime(end);
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDate='" + startDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endDate='" + endDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", eventId=" + eventId +
                ", position=" + position +
                ", beforeColspan=" + beforeColspan +
                ", colspan=" + colspan +
                '}';
    }
}
