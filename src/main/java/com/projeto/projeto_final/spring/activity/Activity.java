package com.projeto.projeto_final.spring.activity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projeto.projeto_final.spring.audit_log.AuditListener;
import com.projeto.projeto_final.spring.subactivity.SubActivity;
import com.projeto.projeto_final.spring.board.Board;
import com.projeto.projeto_final.spring.subactivity.SubActivityDTO;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import com.projeto.projeto_final.spring.utils.EntityUtils;
import jakarta.persistence.*;
import lombok.*;
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
@Builder
@Entity
@EntityListeners(AuditListener.class)
@Table(name="activities")
public class Activity implements Serializable {
    @Serial
    private static final long serialVersionUID = 6L;

    @Transient
    private Activity oldState;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private DeletionStatus deletionStatus = DeletionStatus.ACTIVE;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<SubActivity> subActivities = new HashSet<>();

    public Activity(int id, String title, String description, Board board) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.board = board;
    }

    public void setOldState(Activity oldState) {
        this.oldState = EntityUtils.clone(oldState);
    }

    public ActivityDTO convertToDTO(Activity activity) {
        return ActivityDTO
                .builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .boardId(activity.getBoard().getId())
                .build();
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", deletionStatus=" + deletionStatus +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", boardId=" + board.getId() +
                '}';
    }
}
