package com.projeto.projeto_final.spring.event;

import com.projeto.projeto_final.spring.activity.ActivityDTO;
import com.projeto.projeto_final.spring.board.BoardDTO;
import com.projeto.projeto_final.spring.subactivity.SubActivityDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsDTO {
    private BoardDTO board;
    private List<ActivitySubactivitiesDTO> activities;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivitySubactivitiesDTO {
        private ActivityDTO activity;
        private List<SubActivityDTO> subActivities;

        @Override
        public String toString() {
            return "activity: " + this.activity +
                    " | subActivities: " + this.subActivities.toString();
        }
    }

    @Override
    public String toString() {
        return "board: " + this.board +
                " | activities: " + this.activities.toString();
    }
}