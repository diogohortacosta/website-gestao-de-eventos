package com.projeto.projeto_final.spring.chat;

import com.projeto.projeto_final.spring.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="chat_messages")
public class ChatMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 7L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_username", referencedColumnName = "username", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_username", referencedColumnName = "username", nullable = false)
    private User recipient;

    private String content;

    private Date timestamp;

    @Override
    public String toString() {
        return "id: " + this.id +
                " | chatId: " + this.chatId +
                " | senderId: " + this.sender.getUsername() +
                " | recipientId: " + this.recipient.getUsername() +
                " | content: " + this.content +
                " | timestamp: " + this.timestamp;
    }
}
