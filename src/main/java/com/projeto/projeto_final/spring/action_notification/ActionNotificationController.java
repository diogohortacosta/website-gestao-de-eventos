package com.projeto.projeto_final.spring.action_notification;

import com.projeto.projeto_final.spring.activity.ActivityDTO;
import com.projeto.projeto_final.spring.subactivity.SubActivityDTO;
import com.projeto.projeto_final.spring.task.TaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ActionNotificationController {
    @Autowired
    private SimpMessagingTemplate template;

    public void notifyUser(int eventId, String username, String action) {
        template.convertAndSend("/event/" + eventId + "/" + username, new ActionNotification<>(action, null));
    }

    public void notifyEditors(int eventId, String action,  List<String> listEditors) {
        template.convertAndSend("/event/" + eventId + "/editors", new ActionNotification<>(action, listEditors));
    }

    public void notifyStatus(int eventId, String action,  String status) {
        template.convertAndSend("/event/" + eventId + "/status", new ActionNotification<>(action, status));
    }

    public void notifyTask(int eventId, String action, TaskDTO task) {
        template.convertAndSend("/event/" + eventId + "/tasks", new ActionNotification<>(action, task));
    }

    public void notifyActivity(int eventId, String action, ActivityDTO activity) {
        template.convertAndSend("/event/" + eventId + "/activities", new ActionNotification<>(action, activity));
    }

    public void notifySubActivity(int eventId, String action, SubActivityDTO subActivity) {
        template.convertAndSend("/event/" + eventId + "/subactivities", new ActionNotification<>(action, subActivity));
    }
}
