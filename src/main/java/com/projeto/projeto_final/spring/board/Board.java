package com.projeto.projeto_final.spring.board;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projeto.projeto_final.spring.activity.Activity;
import com.projeto.projeto_final.spring.event.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="boards")
public class Board implements Serializable {
    @Serial
    private static final long serialVersionUID = 5L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int position;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Activity> activities = new HashSet<>();

    public Board(String name, int position, Event event) {
        this.name = name;
        this.position = position;
        this.event = event;
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", eventId=" + event.getId() +
                '}';
    }
}
