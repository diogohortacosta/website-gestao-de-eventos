package com.projeto.projeto_final.spring.event;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private int id;
    private String title;
    private Boolean allDay = false;
    private LocalDateTime start;
    private LocalDateTime end;
    private String backgroundColor = "#0088ff";
    private String textColor = "ffffff";
    private ExtendedProps extendedProps;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtendedProps {
        private String description;
        private String creator;
        private String status;
    }

    @Override
    public String toString() {
        return "id: " + this.id +
                " | title: " + this.title +
                " | allDay: " + this.allDay +
                " | start: " + this.start +
                " | end: " + this.end +
                " | backgroundColor: " + this.backgroundColor +
                " | textColor: " + this.textColor +
                " | description: " + this.getExtendedProps().getDescription() +
                " | creator: " + this.getExtendedProps().getCreator() +
                " | status: " + this.getExtendedProps().getStatus();
    }
}
