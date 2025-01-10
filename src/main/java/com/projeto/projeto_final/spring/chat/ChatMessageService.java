package com.projeto.projeto_final.spring.chat;

import com.projeto.projeto_final.spring.chatroom.ChatRoom;
import com.projeto.projeto_final.spring.chatroom.ChatRoomService;
import com.projeto.projeto_final.spring.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository repository;
    
    @Autowired
    private ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessageDTO chatMessage) {
        String chatId = chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow(); // You can create your own dedicated exception
        ChatMessage message = ChatMessage
                .builder()
                .chatId(chatId)
                .sender(userRepository.findByUsername(chatMessage.getSenderId()))
                .recipient(userRepository.findByUsername(chatMessage.getRecipientId()))
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getTimestamp())
                .build();

        repository.save(message);
        return message;
    }

    public List<ChatMessageDTO> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(id ->
                repository.findByChatId(id).stream()
                        .map(this::convertToDTO) // Converte cada ChatMessage em ChatMessageDTO
                        .collect(Collectors.toList()) // Coleta todos em uma lista
        ).orElse(new ArrayList<>());
    }

    private ChatMessageDTO convertToDTO(ChatMessage chatMessage) {
        return ChatMessageDTO.builder()
                .id(String.valueOf(chatMessage.getId()))
                .senderId(chatMessage.getSender().getUsername())
                .recipientId(chatMessage.getRecipient().getUsername())
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getTimestamp())
                .build();
    }
}
