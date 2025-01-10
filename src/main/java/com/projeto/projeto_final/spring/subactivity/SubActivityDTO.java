package com.projeto.projeto_final.spring.subactivity;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubActivityDTO {
    private int id;
    private String text;
    private boolean checked;
    private int activityId;

    @Override
    public String toString() {
        return "SubActivityDTO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", checked=" + checked +
                ", activityId=" + activityId +
                '}';
    }
}
