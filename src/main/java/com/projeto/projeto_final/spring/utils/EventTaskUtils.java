package com.projeto.projeto_final.spring.utils;

import com.projeto.projeto_final.spring.activity.ActivityDTO;
import com.projeto.projeto_final.spring.event.EventDetailsDTO;
import com.projeto.projeto_final.spring.board.BoardDTO;
import com.projeto.projeto_final.spring.event.Event;
import com.projeto.projeto_final.spring.subactivity.SubActivityDTO;
import com.projeto.projeto_final.spring.task.TaskDTO;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventTaskUtils {
    public static List<String> generateFormattedDayList(LocalDateTime start, LocalDateTime end) {
        List<String> dates = new ArrayList<>();

        long numOfDaysBetween;

        if (end.getHour() == 0 && end.getMinute() == 0) {
            numOfDaysBetween = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate()) - 1;
        }
        else {
            numOfDaysBetween = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (int i = 0; i <= numOfDaysBetween; i++) {
            dates.add(start.plusDays(i).format(formatter));
        }

        return dates;
    }

    public static List<String> generateHourList(LocalDateTime start, LocalDateTime end) {
        List<String> hours = new ArrayList<>();
        long durationHours = Duration.between(start.withMinute(0).withSecond(0).withNano(0), end.withMinute(0).withSecond(0).withNano(0)).toHours();

        if (end.getMinute() == 0) {
            durationHours -= 1;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime currentDateTime = start.withMinute(0).withSecond(0).withNano(0);

        for (int i = 0; i < durationHours + 1; i++) {
            hours.add(currentDateTime.format(formatter));
            currentDateTime = currentDateTime.plusHours(1);
        }

        return hours;
    }

    public static List<Integer> calculateDayColspan(LocalDateTime start, LocalDateTime end) {
        List<Integer> colspans = new ArrayList<>();
        int colspan;

        if (start.toLocalDate().isEqual(end.toLocalDate())) {
            if (end.getMinute() != 0)
                colspan = end.getHour() - start.getHour() + 1;
            else
                colspan = end.getHour() - start.getHour();

            colspans.add(colspan);
            return colspans;
        }

        long numOfDaysBetween = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());

        for (int i = 0; i <= numOfDaysBetween; i++) {
            if (i == 0) { // Se for o primeiro dia
                colspan = 24 - start.getHour();
            }
            else if (i == numOfDaysBetween) { // Se for o último dia
                if (end.getMinute() != 0)
                    colspan = end.getHour() + 1;
                else
                    colspan = end.getHour();
            }
            else { // Se for o restante
                colspan = 24;
            }
            colspans.add(colspan);
        }

        return colspans;
    }

    public static boolean verifyTaskDates(TaskDTO task, LocalDateTime startEvent, LocalDateTime endEvent) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        String dateStringStart = task.getStartDate() + "T" + task.getStartTime();
        String dateStringEnd = task.getEndDate() + "T" + task.getEndTime();

        LocalDateTime startDateTime = LocalDateTime.parse(dateStringStart, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(dateStringEnd, formatter);

        return (startDateTime.isAfter(startEvent) || startDateTime.isEqual(startEvent)) &&
                (endDateTime.isBefore(endEvent) || endDateTime.isEqual(endEvent));
    }

    public static void setTaskColspan(TaskDTO task, LocalDateTime startEvent, LocalDateTime endEvent) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        if (!verifyTaskDates(task, startEvent, endEvent)) {
            task.setColspan(-1);
            task.setBeforeColspan(-1);
        }
        else {
            String startDateString = task.getStartDate() + "T" + task.getStartTime();
            String endDateString = task.getEndDate() + "T" + task.getEndTime();

            LocalDateTime startDateTime = LocalDateTime.parse(startDateString, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endDateString, formatter);

            long colspan = Duration.between(startDateTime.withMinute(0).withSecond(0).withNano(0), endDateTime.withMinute(0).withSecond(0).withNano(0)).toHours();
            long beforeColspan = Duration.between(startEvent.withMinute(0).withSecond(0).withNano(0), startDateTime.withMinute(0).withSecond(0).withNano(0)).toHours();

            if (endDateTime.getMinute() != 0) {
                colspan += 1;
            }

            task.setColspan(((int) colspan));
            task.setBeforeColspan(((int) beforeColspan));
        }
    }

    public static List<DateColspanDTO> generateDateColspanList(LocalDateTime start, LocalDateTime end) {
        List<String> dates = generateFormattedDayList(start, end);
        List<Integer> colspans = calculateDayColspan(start, end);
        List<DateColspanDTO> dateList = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {
            DateColspanDTO newDate = new DateColspanDTO(dates.get(i), colspans.get(i));
            dateList.add(newDate);
        }

        return dateList;
    }

    public static List<EventDetailsDTO> generateBoardActivitiesSubActivitiesList(List<BoardDTO> boardList, List<ActivityDTO> activityList, List<SubActivityDTO> subActivityList) {
        Map<Integer, List<SubActivityDTO>> activitySubactivitiesMap = new HashMap<>();
        for (SubActivityDTO subActivity : subActivityList) {
            int activityId = subActivity.getActivityId();
            if (!activitySubactivitiesMap.containsKey(activityId)) {
                activitySubactivitiesMap.put(activityId, new ArrayList<>());
            }
            activitySubactivitiesMap.get(activityId).add(subActivity);
        }

        Map<Integer, List<ActivityDTO>> boardActivitiesMap = new HashMap<>();
        for (ActivityDTO activity : activityList) {
            int boardId = activity.getBoardId();
            if (!boardActivitiesMap.containsKey(boardId)) {
                boardActivitiesMap.put(boardId, new ArrayList<>());
            }
            boardActivitiesMap.get(boardId).add(activity);
        }

        List<EventDetailsDTO> eventDetailsList = new ArrayList<>();

        for (BoardDTO board : boardList) {
            List<EventDetailsDTO.ActivitySubactivitiesDTO> activitySubactivitiesList = new ArrayList<>();

            List<ActivityDTO> relatedActivities = boardActivitiesMap.get(board.getId());
            if (relatedActivities != null) {
                for (ActivityDTO activity : relatedActivities) {
                    List<SubActivityDTO> relatedSubActivities = activitySubactivitiesMap.get(activity.getId());

                    EventDetailsDTO.ActivitySubactivitiesDTO activitySubactivitiesDTO = new EventDetailsDTO.ActivitySubactivitiesDTO(
                            activity,
                            relatedSubActivities != null ? relatedSubActivities : new ArrayList<>()
                    );
                    activitySubactivitiesList.add(activitySubactivitiesDTO);
                }
            }

            EventDetailsDTO eventDetailsDTO = new EventDetailsDTO(board, activitySubactivitiesList);
            eventDetailsList.add(eventDetailsDTO);
        }

        return eventDetailsList;
    }

    public static boolean isTaskWithinEvent(TaskDTO task, Event event) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate taskStartDate = LocalDate.parse(task.getStartDate(), dateFormatter);
        LocalTime taskStartTime = LocalTime.parse(task.getStartTime(), timeFormatter);
        LocalDateTime taskStartDateTime = LocalDateTime.of(taskStartDate, taskStartTime);

        LocalDate taskEndDate = LocalDate.parse(task.getEndDate(), dateFormatter);
        LocalTime taskEndTime = LocalTime.parse(task.getEndTime(), timeFormatter);
        LocalDateTime taskEndDateTime = LocalDateTime.of(taskEndDate, taskEndTime);

        // Comparando os intervalos de tempo
        return !taskStartDateTime.isBefore(event.getStartDate()) &&
                !taskEndDateTime.isAfter(event.getEndDate());
    }
}
