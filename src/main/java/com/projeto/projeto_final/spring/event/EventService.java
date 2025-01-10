package com.projeto.projeto_final.spring.event;

import com.projeto.projeto_final.spring.activity.ActivityRepository;
import com.projeto.projeto_final.spring.board.BoardRepository;
import com.projeto.projeto_final.spring.subactivity.SubActivityRepository;
import com.projeto.projeto_final.spring.task.Task;
import com.projeto.projeto_final.spring.task.TaskRepository;
import com.projeto.projeto_final.spring.user.UserRepository;
import com.projeto.projeto_final.spring.user.User;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SubActivityRepository subActivityRepository;

    public Event save(EventDTO newEvent) {
        User user = userRepository.findByUsername(newEvent.getExtendedProps().getCreator());

        Event event = new Event();
        event.setTitle(newEvent.getTitle());
        event.setDescription(newEvent.getExtendedProps().getDescription());
        event.setAllDay(newEvent.getAllDay());
        event.setStartDate(newEvent.getStart());
        event.setEndDate(newEvent.getEnd());
        event.setBackgroundColor(newEvent.getBackgroundColor());
        event.setTextColor(newEvent.getTextColor());
        event.setUser(user);

        return eventRepository.save(event);
    }

    public Event updateEvent(int id, EventDTO newEvent) {
        Event event = eventRepository.findById(id);

        event.setOldState(event);
        List<String> editorsList = new ArrayList<>();
        for (User editor : event.getEditors()) {
            editorsList.add(editor.getUsername());
        }
        event.setOldEditors(editorsList);

        event.setTitle(newEvent.getTitle());
        event.setDescription(newEvent.getExtendedProps().getDescription());
        event.setAllDay(newEvent.getAllDay());
        event.setStartDate(newEvent.getStart());
        event.setEndDate(newEvent.getEnd());
        event.setBackgroundColor(newEvent.getBackgroundColor());
        event.setTextColor(newEvent.getTextColor());
        switch (newEvent.getExtendedProps().getStatus()) {
            case "Pending" -> event.setStatus(Status.PENDING);
            case "Inprogress" -> event.setStatus(Status.INPROGRESS);
            case "Confirmed" -> event.setStatus(Status.CONFIRMED);
            case "Finished" -> event.setStatus(Status.FINISHED);
            case "Canceled" -> event.setStatus(Status.CANCELED);
            case "Rescheduled" -> event.setStatus(Status.RESCHEDULED);
        }

        return eventRepository.save(event);
    }

    public void updateEventStatus(Event event, String newStatus) {
        event.setOldState(event);
        List<String> editorsList = new ArrayList<>();
        for (User editor : event.getEditors()) {
            editorsList.add(editor.getUsername());
        }
        event.setOldEditors(editorsList);

        switch (newStatus) {
            case "Pending" -> event.setStatus(Status.PENDING);
            case "Inprogress" -> event.setStatus(Status.INPROGRESS);
            case "Confirmed" -> event.setStatus(Status.CONFIRMED);
            case "Finished" -> event.setStatus(Status.FINISHED);
            case "Canceled" -> event.setStatus(Status.CANCELED);
            case "Rescheduled" -> event.setStatus(Status.RESCHEDULED);
            default -> throw new IllegalArgumentException("Status desconhecido: " + newStatus);
        }

        eventRepository.save(event);
    }

    public void deleteById(int id) {
        Event event = eventRepository.findById(id);

        event.setOldState(event);
        List<String> editorsList = new ArrayList<>();
        for (User editor : event.getEditors()) {
            editorsList.add(editor.getUsername());
        }
        event.setOldEditors(editorsList);

        event.setDeletionStatus(DeletionStatus.DELETED);

        eventRepository.save(event);
    }

    public void addListEditor(Event event, List<String> listEditors) {
        Set<User> editors = new HashSet<>();

        for (String username : listEditors) {
            if (!event.getUser().getUsername().equals(username)) {
                User editor = userRepository.findByUsername(username);
                editors.add(editor);
            }
        }

        event.setOldState(event);
        List<String> editorsList = new ArrayList<>();
        for (User editor : event.getEditors()) {
            editorsList.add(editor.getUsername());
        }
        event.setOldEditors(editorsList);

        event.setEditors(editors);

        eventRepository.save(event);
    }

    public List<EventDTO> findAllActiveEvents() {
        List<Event> events = eventRepository.findAllActive();
        return events.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public List<EventDTO> findActiveEventsUsername(String username) {
        List<Event> events = eventRepository.findByUsername(username);
        return events.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public Event findById(int id) {
        return eventRepository.findById(id);
    }

    public List<String> getListEditors(Event event) {
        List<String> listEditors = new ArrayList<>();

        for (User editor : event.getEditors()) {
            listEditors.add(editor.getUsername());
        }

        return listEditors;
    }

    public boolean canEdit(Event event, String username) {
        User user = userRepository.findByUsername(username);

        boolean canEdit = false;

        if (event.getUser().equals(user)) {
            canEdit = true;
        }
        else if (event.getEditors().contains(user)) {
            canEdit = true;
        }

        return canEdit;
    }

    public EventIDs getRelatedEventIDs(int id) {
        EventIDs eventIDs = new EventIDs();

        eventIDs.setEventId(id);
        eventIDs.setTaskIds(taskRepository.findAllIDsByEventId(id));
        eventIDs.setBoardIds(boardRepository.findAllIDsByEventId(id));
        eventIDs.setActivityIds(activityRepository.findAllIDsByEventId(id));
        eventIDs.setSubActivityIds(subActivityRepository.findAllIDsByEventId(id));

        return eventIDs;
    }

    public EventDTO convertEntityToDto(Event event) {
        EventDTO eventDto = new EventDTO();
        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setStart(event.getStartDate());
        eventDto.setEnd(event.getEndDate());
        eventDto.setAllDay(event.getAllDay());
        eventDto.setBackgroundColor(event.getBackgroundColor());
        eventDto.setTextColor(event.getTextColor());

        EventDTO.ExtendedProps extendedProps = getExtendedProps(event);

        eventDto.setExtendedProps(extendedProps);

        return eventDto;
    }

    private static EventDTO.ExtendedProps getExtendedProps(Event event) {
        EventDTO.ExtendedProps extendedProps = new EventDTO.ExtendedProps();
        extendedProps.setDescription(event.getDescription());
        extendedProps.setCreator(event.getUser().getUsername());
        switch (event.getStatus()) {
            case PENDING -> extendedProps.setStatus("Pending");
            case INPROGRESS -> extendedProps.setStatus("Inprogress");
            case CONFIRMED -> extendedProps.setStatus("Confirmed");
            case FINISHED -> extendedProps.setStatus("Finished");
            case CANCELED -> extendedProps.setStatus("Canceled");
            case RESCHEDULED -> extendedProps.setStatus("Rescheduled");
        }
        return extendedProps;
    }
}
