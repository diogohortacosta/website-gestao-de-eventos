package com.projeto.projeto_final.spring.event;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTOLog {
    private int id;
    private String title;
    private String description;
    private String status;
    private Boolean allDay;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String backgroundColor;
    private String textColor;
    private String creator;
    private List<String> editors;

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
        return "EventDTOLog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", allDay=" + allDay +
                ", startDate='" + startDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endDate='" + endDate + '\'' +
                ", endTime='" + endTime + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", textColor='" + textColor + '\'' +
                ", creator='" + creator + '\'' +
                ", editors=" + editors.toString() +
                '}';
    }
}
