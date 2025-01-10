package com.projeto.projeto_final.spring.subactivity;

import com.projeto.projeto_final.spring.activity.Activity;
import com.projeto.projeto_final.spring.activity.ActivityDTO;
import com.projeto.projeto_final.spring.activity.ActivityRepository;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubActivityService {
    @Autowired
    private ActivityRepository aRepository;

    @Autowired
    private SubActivityRepository saRepository;

    public SubActivity save(SubActivityDTO newSubActivity) {
        SubActivity subActivity = SubActivity
                .builder()
                .deletionStatus(DeletionStatus.ACTIVE)
                .text(newSubActivity.getText())
                .checked(false)
                .activity(aRepository.findById(newSubActivity.getActivityId()))
                .build();

        return saRepository.save(subActivity);
    }

    public SubActivity update(int id, SubActivityDTO newSubActivity) {
        SubActivity subActivity = saRepository.findById(id);

        subActivity.setOldState(subActivity);

        subActivity.setText(newSubActivity.getText());
        subActivity.setChecked(newSubActivity.isChecked());

        return saRepository.save(subActivity);
    }

    public SubActivity deleteById(int id) {
        SubActivity subActivity = saRepository.findById(id);

        subActivity.setOldState(subActivity);

        subActivity.setDeletionStatus(DeletionStatus.DELETED);

        return saRepository.save(subActivity);
    }

    public SubActivity findById(int id) {
        return saRepository.findById(id);
    }

    public List<SubActivityDTO> findActiveSubActivitiesEventId(int id) {
        List<SubActivity> subActivities = saRepository.findAllActiveByEventId(id);
        return subActivities.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public SubActivityDTO convertEntityToDto(SubActivity subActivity) {
        return SubActivityDTO
                .builder()
                .id(subActivity.getId())
                .text(subActivity.getText())
                .checked(subActivity.isChecked())
                .activityId(subActivity.getActivity().getId())
                .build();
    }
}
