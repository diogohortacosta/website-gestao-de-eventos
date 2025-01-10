package com.projeto.projeto_final.spring.activity;

import com.projeto.projeto_final.spring.board.BoardRepository;
import com.projeto.projeto_final.spring.board.Board;
import com.projeto.projeto_final.spring.utils.DeletionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private BoardRepository boardRepository;

    public Activity save(ActivityDTO newActivity) {
        Board board = boardRepository.findById(newActivity.getBoardId());

        Activity activity = new Activity();
        activity.setTitle(newActivity.getTitle());
        activity.setDescription(newActivity.getDescription());
        activity.setBoard(board);

        return activityRepository.save(activity);
    }

    public Activity update(int id, ActivityDTO newActivity) {
        Activity activity = activityRepository.findById(id);

        activity.setOldState(activity);

        activity.setTitle(newActivity.getTitle());
        activity.setDescription(newActivity.getDescription());

        return activityRepository.save(activity);
    }

    public Activity deleteById(int id) {
        Activity activity = activityRepository.findById(id);

        activity.setOldState(activity);

        activity.setDeletionStatus(DeletionStatus.DELETED);

        return activityRepository.save(activity);
    }

    public Activity move(int boardId, int activityId) {
        Activity activity = activityRepository.findById(activityId);
        Board newBoard = boardRepository.findById(boardId);

        activity.setOldState(activity);

        activity.setBoard(newBoard);

        return activityRepository.save(activity);
    }

    public Activity findById(int id) {
        return activityRepository.findById(id);
    }

    public List<ActivityDTO> findActiveActivitiesEventId(int id) {
        List<Activity> activities = activityRepository.findAllActiveByEventId(id);
        return activities.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public ActivityDTO convertEntityToDto(Activity activity) {
        return ActivityDTO
                .builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .boardId(activity.getBoard().getId())
                .build();
    }
}
