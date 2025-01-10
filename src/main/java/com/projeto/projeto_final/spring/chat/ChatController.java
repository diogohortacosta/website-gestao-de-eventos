package com.projeto.projeto_final.spring.chat;

import com.projeto.projeto_final.spring.user.UserDTO;
import com.projeto.projeto_final.spring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/chat")
    public String seeCalendars(Model model, Principal principal) {
        UserDTO user = userService.convertEntityToDto(userService.findByUsername(principal.getName()));

        model.addAttribute("user", user);

        return "chat";
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
                new ChatMessageDTO(
                        savedMsg.getChatId(),
                        savedMsg.getSender().getUsername(),
                        savedMsg.getRecipient().getUsername(),
                        savedMsg.getContent(),
                        savedMsg.getTimestamp()
                )
        );
    }
}
