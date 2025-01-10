package com.projeto.projeto_final.spring.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;
    private String name;
    private String username;
    private Status status;

    @Override
    public String toString() {
        return "id: " + this.id +
                " | name: " + this.name +
                " | username: " + this.username;
    }
}
