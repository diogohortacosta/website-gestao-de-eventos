package com.projeto.projeto_final.spring.chat;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;
}
