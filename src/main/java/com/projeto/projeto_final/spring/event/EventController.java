package com.projeto.projeto_final.spring.event;

import com.projeto.projeto_final.spring.action_notification.ActionNotificationController;
import com.projeto.projeto_final.spring.activity.ActivityDTO;
import com.projeto.projeto_final.spring.board.BoardDTO;
import com.projeto.projeto_final.spring.subactivity.SubActivityDTO;
import com.projeto.projeto_final.spring.subactivity.SubActivityService;
import com.projeto.projeto_final.spring.utils.DateColspanDTO;
import com.projeto.projeto_final.spring.task.Task;
import com.projeto.projeto_final.spring.activity.ActivityService;
import com.projeto.projeto_final.spring.board.BoardService;
import com.projeto.projeto_final.spring.task.TaskDTO;
import com.projeto.projeto_final.spring.task.TaskService;
import com.projeto.projeto_final.spring.user.UserService;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import com.projeto.projeto_final.spring.utils.EventTaskUtils;
import com.projeto.projeto_final.spring.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EventController {
    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private SubActivityService subActivityService;

    @Autowired
    private ActionNotificationController notification;

    @GetMapping("/calendar")
    public String seeCalendars(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        model.addAttribute("username", username);

        return "events_calendar";
    }

    @PostMapping("/events/add")
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDto, Principal principal) {
        // Verificar se os campos obrigatórios estão preenchidos
        if (eventDto.getTitle() == null || eventDto.getExtendedProps().getDescription() == null ||
                eventDto.getStart() == null || eventDto.getEnd() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Os campos obrigatórios não podem ser nulos."));
        }

        try {
            eventDto.getExtendedProps().setCreator(principal.getName());
            Event savedEvent = eventService.save(eventDto);
            eventDto.setId(savedEvent.getId());

            return ResponseEntity.ok(eventDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao adicionar o evento."));
        }
    }

    @DeleteMapping("/events/delete/{eventid}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventid") int id, Principal principal) {
        try {
            Event event = eventService.findById(id);

            // Verifica se o evento existe antes de tentar excluir
            if (event != null) {
                // Verifica se o user é o criador do evento
                if (event.getUser().getUsername().equals(principal.getName())) {
                    eventService.deleteById(id);
                    return ResponseEntity.ok(new ResponseMessage("Evento excluído com sucesso."));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para excluir este evento."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Evento não encontrado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao excluir o evento."));
        }
    }

    @PutMapping("/events/update/{eventid}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventid") int id, @RequestBody EventDTO newEvent, Principal principal) {
        try {
            Event event = eventService.findById(id);

            // Verifica se o evento existe antes de tentar dar update
            if (event != null) {
                // Verifica se o user é o criador do evento
                if (event.getUser().getUsername().equals(principal.getName())) {
                    LocalDateTime beforeStart = event.getStartDate();

                    EventDTO updatedEvent = eventService.convertEntityToDto(eventService.updateEvent(id, newEvent));

                    // Verifica se o evento tem tarefas e se as datas mudaram, caso tenham mudado é preciso alterar minimamente as datas das tarefas associadas
                    if (!event.getTasks().isEmpty() && !beforeStart.isEqual(newEvent.getStart())) {
                        Duration duration = Duration.between(beforeStart, newEvent.getStart());
                        for (Task task : event.getTasks()) {
                            task.setSkipPreUpdate(true);
                            TaskDTO newTask = taskService.convertEntityToDto(task);
                            newTask.setDates(task.getStartDate().plus(duration), task.getEndDate().plus(duration));
                            taskService.updateTask(task.getId(), newTask);
                        }
                    }

                    return ResponseEntity.ok(updatedEvent);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para atualizar este evento."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Evento não encontrado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao atualizar o evento."));
        }
    }

    @GetMapping("/event/{eventid}")
    public String seeEvent(@PathVariable("eventid") int id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Event event = eventService.findById(id);

        if(event != null) {
            if (event.getDeletionStatus() == DeletionStatus.DELETED) {
                return "redirect:/calendar";
            }

            EventDTO eventDto = eventService.convertEntityToDto(event);

            boolean canEdit = eventService.canEdit(event, username);

            List<String> listEditors = eventService.getListEditors(event);
            String editorsString = String.join(", ", listEditors);

            List<String> listUsernames = userService.getListAllUsernames();
            listUsernames.remove(username);

            List<TaskDTO> eventTasks = taskService.findActiveTasksEventId(event.getId());

            for (TaskDTO task : eventTasks) {
                EventTaskUtils.setTaskColspan(task, eventDto.getStart(), eventDto.getEnd());
            }

            Duration duration = Duration.between(event.getStartDate(), event.getEndDate());
            long days = duration.toDays();
            long hours = duration.minusDays(days).toHours();
            long minutes = duration.minusDays(days).minusHours(hours).toMinutes();

            String formattedDuration;
            if (days > 0) {
                formattedDuration = String.format("%d Dias, %d Horas e %d Minutos", days, hours, minutes);
            } else if (hours > 0) {
                formattedDuration = String.format("%d Horas e %d Minutos", hours, minutes);
            } else {
                formattedDuration = String.format("%d Minutos", minutes);
            }

            List<DateColspanDTO> dateList = EventTaskUtils.generateDateColspanList(event.getStartDate(), event.getEndDate());
            List<String> hourList = EventTaskUtils.generateHourList(event.getStartDate(), event.getEndDate());

            List<BoardDTO> boardList = boardService.findBoardsEventId(event.getId());
            List<ActivityDTO> activityList = activityService.findActiveActivitiesEventId(event.getId());
            List<SubActivityDTO> subActivityList = subActivityService.findActiveSubActivitiesEventId(event.getId());

            List<EventDetailsDTO> eventDetailsList = EventTaskUtils.generateBoardActivitiesSubActivitiesList(boardList, activityList, subActivityList);

            model.addAttribute("username", username);
            model.addAttribute("event", eventDto);
            model.addAttribute("canEdit", canEdit);
            model.addAttribute("listEditors", listEditors);
            model.addAttribute("editorsString", editorsString);
            model.addAttribute("listUsernames", listUsernames);
            model.addAttribute("eventDuration", formattedDuration);
            model.addAttribute("tasks", eventTasks);
            model.addAttribute("eventDetailsList", eventDetailsList);
            model.addAttribute("dateList", dateList);
            model.addAttribute("hourList", hourList);

            return "planning_event";
        }
        else {
            return "redirect:/calendar";
        }
    }

    @PostMapping("/event/update/{eventid}/editors")
    public ResponseEntity<?> updateEditors(@PathVariable("eventid") int id, @RequestBody List<String> listEditors, Principal principal) {
        try {
            Event existingEvent = eventService.findById(id);

            // Verifica se o evento existe antes de tentar dar update
            if (existingEvent != null) {
                // Verifica se o user é o criador do evento
                if (existingEvent.getUser().getUsername().equals(principal.getName())) {
                    List<String> oldUsers = new ArrayList<>(existingEvent.getStringListEditors());

                    // Encontrar removidos (oldUsers - newUsers)
                    List<String> removedUsers = new ArrayList<>(oldUsers);
                    removedUsers.removeAll(listEditors);

                    // Encontrar adicionados (newUsers - oldUsers)
                    List<String> addedUsers = new ArrayList<>(listEditors);
                    addedUsers.removeAll(oldUsers);

                    eventService.addListEditor(existingEvent, listEditors);

                    for (String username : removedUsers) {
                        notification.notifyUser(existingEvent.getId(), username, "remove");
                    }

                    for (String username : addedUsers) {
                        notification.notifyUser(existingEvent.getId(), username, "add");
                    }

                    notification.notifyEditors(existingEvent.getId(), "update", listEditors);

                    return ResponseEntity.ok(listEditors);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para adicionar editores a este evento."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Evento não encontrado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao adicionar editores ao evento."));
        }
    }

    @PutMapping("/event/update/{eventid}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("eventid") int id, @RequestBody String status, Principal principal) {
        if (!status.equalsIgnoreCase("Pending") && !status.equalsIgnoreCase("Inprogress") && !status.equalsIgnoreCase("Confirmed") &&
                !status.equalsIgnoreCase("Finished") && !status.equalsIgnoreCase("Canceled") && !status.equalsIgnoreCase("Rescheduled")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Estado não aceite."));
        }

        try {
            Event existingEvent = eventService.findById(id);

            // Verifica se o evento existe antes de tentar dar update
            if (existingEvent != null) {
                // Verifica se o user tem permissão para editar
                if (eventService.canEdit(existingEvent, principal.getName())) {
                    eventService.updateEventStatus(existingEvent, status);

                    notification.notifyStatus(existingEvent.getId(), "update", status);

                    return ResponseEntity.ok(status);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para modificar o estado deste evento."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Evento não encontrado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao atualizar o estado do evento."));
        }
    }
}
