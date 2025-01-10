package com.projeto.projeto_final.spring.action_notification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionNotification<T> {
    private String action;
    private T data;
}
