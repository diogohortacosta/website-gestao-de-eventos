package com.projeto.projeto_final.spring.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private int id;
    private String name;

    @Override
    public String toString() {
        return "BoardDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
