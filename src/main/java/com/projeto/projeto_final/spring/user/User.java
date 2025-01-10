package com.projeto.projeto_final.spring.user;

import com.projeto.projeto_final.spring.chat.ChatMessage;
import com.projeto.projeto_final.spring.event.Event;
import com.projeto.projeto_final.spring.role.Role;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false)
    private String password;

    @Column(nullable = false)
    private Status status = Status.OFFLINE;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Event> eventos = new HashSet<>();

    @ManyToMany(mappedBy = "editors", fetch = FetchType.LAZY)
    private Set<Event> editEvents = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<ChatMessage> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    private Set<ChatMessage> receivedMessages = new HashSet<>();

    public User(String name, String username, String password, Set<Role> roles) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String name, String username, String password, Set<Role> roles, Set<Event> eventos) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.eventos = eventos;
    }

    public User(int id, String name, String username, String password, Set<Role> roles, Set<Event> eventos) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.eventos = eventos;
    }

    public User(int id, String name, String username, String password, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public void addEvent(Event evento) {
        this.eventos.add(evento);
        evento.setUser(this);
    }

    public void removeEvent(Event evento) {
        this.eventos.remove(evento);
        evento.setUser(null);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                '}';
    }
}