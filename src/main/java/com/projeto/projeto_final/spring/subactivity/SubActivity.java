package com.projeto.projeto_final.spring.subactivity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projeto.projeto_final.spring.activity.Activity;
import com.projeto.projeto_final.spring.audit_log.AuditListener;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import com.projeto.projeto_final.spring.utils.EntityUtils;
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
@EntityListeners(AuditListener.class)
@Table(name="subactivities")
public class SubActivity implements Serializable {
    @Serial
    private static final long serialVersionUID = 9L;

    @Transient
    private SubActivity oldState;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private DeletionStatus deletionStatus = DeletionStatus.ACTIVE;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean checked = false;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    @JsonIgnoreProperties("subActivities")
    private Activity activity;

    public void setOldState(SubActivity oldState) {
        this.oldState = EntityUtils.clone(oldState);
    }

    public SubActivityDTO convertToDTO(SubActivity subActivity) {
        return SubActivityDTO
                .builder()
                .id(subActivity.getId())
                .text(subActivity.getText())
                .checked(subActivity.isChecked())
                .activityId(subActivity.getActivity().getId())
                .build();
    }

    @Override
    public String toString() {
        return "SubActivity{" +
                "id=" + id +
                ", deletionStatus=" + deletionStatus +
                ", text='" + text + '\'' +
                ", checked=" + checked +
                ", activityId=" + activity.getId() +
                '}';
    }
}


