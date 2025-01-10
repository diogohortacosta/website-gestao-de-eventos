package com.projeto.projeto_final.spring.event;

import com.projeto.projeto_final.spring.audit_log.AuditListener;
import com.projeto.projeto_final.spring.board.Board;
import com.projeto.projeto_final.spring.task.Task;
import com.projeto.projeto_final.spring.task.TaskDTO;
import com.projeto.projeto_final.spring.user.User;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import com.projeto.projeto_final.spring.utils.EntityUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditListener.class)
@Table(name="events")
public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @Transient
    private Event oldState;

    @Transient
    private List<String> oldEditors;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private DeletionStatus deletionStatus = DeletionStatus.ACTIVE;

    @Column(nullable=false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean allDay;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false)
    private String backgroundColor;

    @Column(nullable = false)
    private String textColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_editors",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> editors = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Board> boards = new HashSet<>(Arrays.asList(
            new Board("To Do",1, this),
            new Board("Doing",2,  this),
            new Board("Review",3, this),
            new Board("Done",4, this)
    ));

    public Event(String title, String description, Boolean allDay, LocalDateTime startDate, LocalDateTime endDate, String backgroundColor, String textColor, User user) {
        this.title = title;
        this.description = description;
        this.allDay = allDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.user = user;
    }

    public Event(int id, String title, String description, Boolean allDay, LocalDateTime startDate, LocalDateTime endDate, String backgroundColor, String textColor, User user) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.allDay = allDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.user = user;
    }

    public void setOldState(Event oldState) {
        this.oldState = EntityUtils.clone(oldState);
    }

    public List<String> getStringListEditors() {
        List<String> listEditors = new ArrayList<>();

        for (User user : this.getEditors()) {
            listEditors.add(user.getUsername());
        }

        return listEditors;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        task.setEvent(this);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
        task.setEvent(null);
    }

    public EventDTOLog convertToDTO(Event event, List<String> editorsList) {
        EventDTOLog eventDto = setEventDTOExceptEditors(event);
        eventDto.setEditors(editorsList);

        return eventDto;
    }

    public EventDTOLog convertToDTO(Event event) {
        EventDTOLog eventDto = setEventDTOExceptEditors(event);
        List<String> editorsList = new ArrayList<>();
        for (User editor : event.getEditors()) {
            editorsList.add(editor.getUsername());
        }
        eventDto.setEditors(editorsList);

        return eventDto;
    }

    private EventDTOLog setEventDTOExceptEditors(Event event) {
        EventDTOLog eventDto = new EventDTOLog();
        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setDescription(event.getDescription());
        switch (event.getStatus()) {
            case PENDING -> eventDto.setStatus("Pending");
            case INPROGRESS -> eventDto.setStatus("Inprogress");
            case CONFIRMED -> eventDto.setStatus("Confirmed");
            case FINISHED -> eventDto.setStatus("Finished");
            case CANCELED -> eventDto.setStatus("Canceled");
            case RESCHEDULED -> eventDto.setStatus("Rescheduled");
        }
        eventDto.setAllDay(event.getAllDay());
        eventDto.setDates(event.getStartDate(), event.getEndDate());
        eventDto.setBackgroundColor(event.getBackgroundColor());
        eventDto.setTextColor(event.getTextColor());
        eventDto.setCreator(event.getUser().getUsername());

        return eventDto;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", deletionStatus=" + deletionStatus +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", allDay=" + allDay +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", textColor='" + textColor + '\'' +
                ", user_id=" + user.getId() +
                '}';
    }
}
