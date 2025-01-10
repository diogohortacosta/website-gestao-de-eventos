package com.projeto.projeto_final.spring.subactivity;

import com.projeto.projeto_final.spring.action_notification.ActionNotificationController;
import com.projeto.projeto_final.spring.activity.Activity;
import com.projeto.projeto_final.spring.activity.ActivityDTO;
import com.projeto.projeto_final.spring.activity.ActivityService;
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
public class SubActivityController {
    @Autowired
    private EventService eService;

    @Autowired
    private ActivityService aService;

    @Autowired
    private SubActivityService saService;

    @Autowired
    private ActionNotificationController notification;

    @PostMapping("/subactivities/add/{activityid}")
    public ResponseEntity<?> createSubActivity(@PathVariable("activityid") int id, @RequestBody SubActivityDTO newSubActivity, Principal principal) {
        if (newSubActivity.getText() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Os campos obrigatórios não podem ser nulos."));
        }

        try {
            Activity existingActivity = aService.findById(id);

            if (existingActivity != null) {
                if (eService.canEdit(existingActivity.getBoard().getEvent(), principal.getName())) {
                    newSubActivity.setActivityId(id);
                    SubActivityDTO subActivity = saService.convertEntityToDto(saService.save(newSubActivity));

                    notification.notifySubActivity(existingActivity.getBoard().getEvent().getId(), "create", subActivity);

                    return ResponseEntity.ok(subActivity);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para adicionar esta atividade."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Atividade não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao adicionar a atividade."));
        }
    }

    @DeleteMapping("/subactivities/delete/{subactivityid}")
    public ResponseEntity<?> deleteSubActivity(@PathVariable("subactivityid") int id, Principal principal) {
        try {
            SubActivity existingSubActivity = saService.findById(id);

            if (existingSubActivity != null) {
                if (eService.canEdit(existingSubActivity.getActivity().getBoard().getEvent(), principal.getName())) {
                    SubActivityDTO subActivity = saService.convertEntityToDto(saService.deleteById(id));

                    notification.notifySubActivity(existingSubActivity.getActivity().getBoard().getEvent().getId(), "delete", subActivity);

                    return ResponseEntity.ok(subActivity);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para excluir esta subatividade."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Subatividade não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao excluir a subatividade."));
        }
    }

    @PutMapping("/subactivities/check/{subactivityid}")
    public ResponseEntity<?> checkSubActivity(@PathVariable("subactivityid") int id, @RequestBody SubActivityDTO newSubActivity, Principal principal) {
        try {
            SubActivity ExistingSubActivity = saService.findById(id);

            if (ExistingSubActivity != null) {
                if (eService.canEdit(ExistingSubActivity.getActivity().getBoard().getEvent(), principal.getName())) {
                    SubActivityDTO subActivity = saService.convertEntityToDto(saService.update(id, newSubActivity));

                    notification.notifySubActivity(ExistingSubActivity.getActivity().getBoard().getEvent().getId(), "check", subActivity);

                    return ResponseEntity.ok(subActivity);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("Sem permissão para marcar esta atividade."));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Atividade não encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Erro ao marcar a atividade."));
        }
    }
}
