package com.projeto.projeto_final.spring.activity;

import com.projeto.projeto_final.spring.action_notification.ActionNotificationController;
import com.projeto.projeto_final.spring.board.BoardService;
import com.projeto.projeto_final.spring.board.Board;
import com.projeto.projeto_final.spring.event.EventService;
import com.projeto.projeto_final.spring.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ActivityController {
    @Autowired
    private EventService eService;

    @Autowired
    private BoardService bService;

    @Autowired
    private ActivityService aService;

    @Autowired
    private ActionNotificationController notification;

    @PostMapping("/activities/add/{eventid}/{boardid}")
    public ResponseEntity<?> createActivity(@PathVariable("eventid") int eventId, @PathVariable("boardid") int boardId, @RequestBody ActivityDTO newActivity, Principal principal) {
        if (newActivity.getTitle() == null || newActivity.getDescription() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Os campos obrigatórios não podem ser nulos."));
        }

        try {
            Board existingBoard = bService.findById(boardId);

            if (existingBoard != null) {
                if (eService.canEdit(existingBoard.getEvent(), principal.getName())) {
                    newActivity.setBoardId(boardId);
                    ActivityDTO activity = aService.convertEntityToDto(aService.save(newActivity));

                    notification.notifyActivity(existingBoard.getEvent().getId(), "create", activity);

                    return ResponseEntity.ok(activity);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para adicionar atividade."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Atividade não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao adicionar a atividade."));
        }
    }

    @DeleteMapping("/activities/delete/{activityid}")
    public ResponseEntity<?> deleteActivity(@PathVariable("activityid") int id, Principal principal) {
        try {
            Activity existingActivity = aService.findById(id);

            if (existingActivity != null) {
                if (eService.canEdit(existingActivity.getBoard().getEvent(), principal.getName())) {
                    ActivityDTO activity = aService.convertEntityToDto(aService.deleteById(id));

                    notification.notifyActivity(existingActivity.getBoard().getEvent().getId(), "delete", activity);

                    return ResponseEntity.ok(activity);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para excluir esta atividade."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Atividade não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao excluir a atividade."));
        }
    }

    @PutMapping("/activities/update/{activityid}")
    public ResponseEntity<?> updateActivity(@PathVariable("activityid") int id, @RequestBody ActivityDTO newActivity, Principal principal) {
        try {
            Activity existingActivity = aService.findById(id);

            if (existingActivity != null) {
                if (eService.canEdit(existingActivity.getBoard().getEvent(), principal.getName())) {
                    // Verificar qual dos dados da tarefa recebida está null, ou seja não foi alterada
                    if (newActivity.getTitle() == null) {
                        newActivity.setTitle(existingActivity.getTitle());
                    }

                    if (newActivity.getDescription() == null) {
                        newActivity.setDescription(existingActivity.getDescription());
                    }

                    ActivityDTO updatedActivity = aService.convertEntityToDto(aService.update(id, newActivity));

                    notification.notifyActivity(existingActivity.getBoard().getEvent().getId(), "update", updatedActivity);

                    return ResponseEntity.ok(updatedActivity);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para atualizar esta atividade."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Atividade não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao atualizar a atividade."));
        }
    }

    @PutMapping("/activities/move/{boardid}/{activityid}")
    public ResponseEntity<?> moveActivity(@PathVariable("boardid") int boardId, @PathVariable("activityid") int activityId, Principal principal) {
        try {
            Board existingBoard = bService.findById(boardId);
            Activity existingActivity = aService.findById(activityId);

            if (existingBoard != null && existingActivity != null) {
                if (eService.canEdit(existingActivity.getBoard().getEvent(), principal.getName())) {
                    if (existingBoard.getEvent() == existingActivity.getBoard().getEvent()) {
                        ActivityDTO activity = aService.convertEntityToDto(aService.move(boardId, activityId));

                        notification.notifyActivity(existingActivity.getBoard().getEvent().getId(), "move", activity);

                        return ResponseEntity.ok(activity);
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Atividade não pode mover para a tabela indicada."));
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para mover esta atividade."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Atividade não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao mover a atividade."));
        }
    }
}
