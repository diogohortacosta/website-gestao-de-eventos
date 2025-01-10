package com.projeto.projeto_final.spring.activity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private int id;
    private String title;
    private String description;
    private int boardId;

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", boardId=" + boardId +
                '}';
    }
}
