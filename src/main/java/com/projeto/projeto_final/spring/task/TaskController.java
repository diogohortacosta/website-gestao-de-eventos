package com.projeto.projeto_final.spring.task;

import com.projeto.projeto_final.spring.action_notification.ActionNotificationController;
import com.projeto.projeto_final.spring.event.Event;
import com.projeto.projeto_final.spring.event.EventService;
import com.projeto.projeto_final.spring.utils.EventTaskUtils;
import com.projeto.projeto_final.spring.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ActionNotificationController notification;

    @PostMapping("/tasks/add")
    public ResponseEntity<?> createTask(@RequestBody TaskDTO newTask, Principal principal) {
        // Verificar se os campos obrigatórios estão preenchidos
        if (newTask.getTitle() == null || newTask.getStartDate() == null || newTask.getStartTime() == null || newTask.getEndDate() == null || newTask.getEndTime() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Os campos obrigatórios não podem ser nulos."));
        }

        try {
            Event existingEvent = eventService.findById(newTask.getEventId());

            // Verifica se o evento com um eventId igual existe
            if (existingEvent != null) {
                // Verifica se o user pode editar
                if (eventService.canEdit(existingEvent, principal.getName())) {
                    // Verificar se as datas estão entre os limites do evento
                    if (EventTaskUtils.isTaskWithinEvent(newTask, existingEvent)) {
                        TaskDTO task = taskService.convertEntityToDto(taskService.saveTask(newTask));
                        EventTaskUtils.setTaskColspan(task, existingEvent.getStartDate(), existingEvent.getEndDate());

                        notification.notifyTask(existingEvent.getId(), "create", task);

                        return ResponseEntity.ok(task);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("As datas e horas não podem estar fora do intervalo permitido."));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para adicionar tarefa a este evento."));
                }
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Evento da tarefa não encontrado."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao adicionar a tarefa."));
        }
    }

    @DeleteMapping("/tasks/delete/{taskid}")
    public ResponseEntity<?> deleteTask(@PathVariable("taskid") int id, Principal principal) {
        try {
            Task existingTask = taskService.findById(id);

            // Verifica se o evento existe antes de tentar excluir
            if (existingTask != null) {
                // Verifica se o user pode editar a tarefa
                if (eventService.canEdit(existingTask.getEvent(), principal.getName())) {
                    TaskDTO task = taskService.convertEntityToDto(taskService.deleteById(id));

                    notification.notifyTask(existingTask.getEvent().getId(), "delete", task);

                    return ResponseEntity.ok(task);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para excluir esta tarefa."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Tarefa não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao excluir a tarefa."));
        }
    }

    @PutMapping("/tasks/update/{taskid}")
    public ResponseEntity<?> updateEvent(@PathVariable("taskid") int id, @RequestBody TaskDTO newTask, Principal principal) {
        try {
            Task existingTask = taskService.findById(id);

            // Verifica se a tarefa existe antes de tentar excluir
            if (existingTask != null) {
                // Verifica se o user pode editar a tarefa
                if (eventService.canEdit(existingTask.getEvent(), principal.getName())) {
                    // Verificar qual dos dados da tarefa recebida está null, ou seja não foi alterada
                    if (newTask.getTitle() == null) {
                        newTask.setTitle(existingTask.getTitle());
                    }

                    if (newTask.getStartDate() == null && newTask.getStartTime() == null) {
                        newTask.setStartDateTime(existingTask.getStartDate());
                    }

                    if (newTask.getEndDate() == null && newTask.getEndTime() == null) {
                        newTask.setEndDateTime(existingTask.getEndDate());
                    }

                    TaskDTO task = taskService.convertEntityToDto(taskService.updateTask(id, newTask));
                    EventTaskUtils.setTaskColspan(task, existingTask.getEvent().getStartDate(), existingTask.getEvent().getEndDate());

                    notification.notifyTask(existingTask.getEvent().getId(), "update", task);

                    return ResponseEntity.ok(task);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para atualizar esta tarefa."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Tarefa não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao atualizar a tarefa."));
        }
    }

    @PutMapping("/tasks/up/{taskid}")
    public ResponseEntity<?> upTask(@PathVariable("taskid") int id, Principal principal) {
        try {
            Task existingTask = taskService.findById(id);

            // Verifica se o evento existe antes de tentar mover para cima
            if (existingTask != null) {
                // Verifica se o user pode editar a tarefa
                if (eventService.canEdit(existingTask.getEvent(), principal.getName())) {
                    TaskDTO task = taskService.convertEntityToDto(taskService.upTask(id));

                    notification.notifyTask(existingTask.getEvent().getId(), "up", task);

                    return ResponseEntity.ok(task);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para mover esta tarefa."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Tarefa não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao mover tarefa."));
        }
    }

    @PutMapping("/tasks/down/{taskid}")
    public ResponseEntity<?> downTask(@PathVariable("taskid") int id, Principal principal) {
        try {
            Task existingTask = taskService.findById(id);

            // Verifica se o evento existe antes de tentar mover para cima
            if (existingTask != null) {
                // Verifica se o user pode editar a tarefa
                if (eventService.canEdit(existingTask.getEvent(), principal.getName())) {
                    TaskDTO task = taskService.convertEntityToDto(taskService.downTask(id));

                    notification.notifyTask(existingTask.getEvent().getId(), "down", task);

                    return ResponseEntity.ok(task);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para mover esta tarefa."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Tarefa não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao mover tarefa."));
        }
    }
}
