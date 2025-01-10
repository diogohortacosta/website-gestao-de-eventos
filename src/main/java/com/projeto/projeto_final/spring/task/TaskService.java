package com.projeto.projeto_final.spring.task;

import com.projeto.projeto_final.spring.utils.DeletionStatus;
import com.projeto.projeto_final.spring.event.EventRepository;
import com.projeto.projeto_final.spring.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EventRepository eventRepository;

    public Task saveTask(TaskDTO taskDto) {
        Task newTask = new Task();
        newTask.setTitle(taskDto.getTitle());
        newTask.setStartDate(taskDto.getStartDate(), taskDto.getStartTime());
        newTask.setEndDate(taskDto.getEndDate(), taskDto.getEndTime());
        Event event = eventRepository.findById(taskDto.getEventId());
        newTask.setEvent(event);
        newTask.setPosition(taskRepository.countActiveByEventId(event.getId()) + 1);

        return taskRepository.save(newTask);
    }

    public Task updateTask(int id, TaskDTO newTask) {
        Task updatedTask = taskRepository.findById(id);

        updatedTask.setOldState(updatedTask);
        updatedTask.setTitle(newTask.getTitle());
        updatedTask.setStartDate(newTask.getStartDate(), newTask.getStartTime());
        updatedTask.setEndDate(newTask.getEndDate(), newTask.getEndTime());

        return taskRepository.save(updatedTask);
    }

    public Task deleteById(int id) {
        Task taskToDelete = findById(id);

        int taskPosition = taskToDelete.getPosition();
        Event event = taskToDelete.getEvent();

        List<Task> tasksToUpdate = taskRepository.findTasksWithPositionGreaterThan(event, taskPosition);
        for (Task task : tasksToUpdate) {
            task.setPosition(task.getPosition() - 1);
            task.setSkipPreUpdate(true);
        }
        taskRepository.saveAll(tasksToUpdate);

        taskToDelete.setOldState(taskToDelete);
        taskToDelete.setSkipPreUpdate(false);
        taskToDelete.setPosition(-taskToDelete.getPosition());
        taskToDelete.setDeletionStatus(DeletionStatus.DELETED);

        return taskRepository.save(taskToDelete);
    }

    public Task upTask(int id) {
        Task task = taskRepository.findById(id);
        Event event = task.getEvent();

        if (task.getPosition() > 1) {
            Task previousTask = taskRepository.findByPositionAndEvent(event, task.getPosition() - 1);

            task.setOldState(task);
            task.setSkipPreUpdate(false);
            task.setPosition(task.getPosition() - 1);

            previousTask.setSkipPreUpdate(true);
            previousTask.setPosition(previousTask.getPosition() + 1);

            taskRepository.saveAll(Arrays.asList(task, previousTask));
        }

        return taskRepository.findById(id);
    }

    public Task downTask(int id) {
        Task task = taskRepository.findById(id);
        Event event = task.getEvent();
        if (task.getPosition() < task.getEvent().getTasks().size()) {
            Task nextTask = taskRepository.findByPositionAndEvent(event, task.getPosition() + 1);

            task.setOldState(task);
            task.setSkipPreUpdate(false);
            task.setPosition(task.getPosition() + 1);

            nextTask.setSkipPreUpdate(true);
            nextTask.setPosition(nextTask.getPosition() - 1);

            taskRepository.saveAll(Arrays.asList(task, nextTask));
        }

        return taskRepository.findById(id);
    }

    public Task findById(int id) {
        return taskRepository.findById(id);
    }

    public List<TaskDTO> findActiveTasksEventId(int eventId) {
        List<Task> tasks = taskRepository.findActiveByEventId(eventId);
        return tasks.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public TaskDTO convertEntityToDto(Task task) {
        TaskDTO taskDto = new TaskDTO();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDates(task.getStartDate(), task.getEndDate());
        taskDto.setEventId(task.getEvent().getId());
        taskDto.setPosition(task.getPosition());

        return taskDto;
    }
}
