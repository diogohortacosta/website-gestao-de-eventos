package com.projeto.projeto_final.spring.event;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventIDs {
    private int eventId;
    private List<Integer> taskIds;
    private List<Integer> boardIds;
    private List<Integer> activityIds;
    private List<Integer> subActivityIds;

    @Override
    public String toString() {
        return "EventIDs{" +
                "eventId=" + eventId +
                ", taskIds=" + taskIds.toString() +
                ", boardIds=" + boardIds.toString() +
                ", activityIds=" + activityIds.toString() +
                ", subActivityIds=" + subActivityIds.toString() +
                '}';
    }
}
