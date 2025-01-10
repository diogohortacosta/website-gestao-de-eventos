package com.projeto.projeto_final.spring.chatroom;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="chat_rooms")
public class ChatRoom implements Serializable {
    @Serial
    private static final long serialVersionUID = 8L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String chatId;

    private String senderId;

    private String recipientId;
}
