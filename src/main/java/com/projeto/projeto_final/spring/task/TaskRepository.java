package com.projeto.projeto_final.spring.task;

import com.projeto.projeto_final.spring.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT t FROM Task t WHERE t.id = ?1")
    Task findById(int id);

    @Query("SELECT t FROM Task t WHERE t.event.id = ?1 AND t.deletionStatus = 0 ORDER BY t.position")
    List<Task> findActiveByEventId(int eventId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.event.id = ?1 AND t.deletionStatus = 0")
    int countActiveByEventId(int eventId);

    @Query("SELECT t FROM Task t WHERE t.event = ?1 AND t.position = ?2")
    Task findByPositionAndEvent(Event event, int position);

    @Query("SELECT t FROM Task t WHERE t.event = ?1 AND t.position > ?2")
    List<Task> findTasksWithPositionGreaterThan(Event event, int position);

    @Query("SELECT t.id FROM Task t WHERE t.event.id = ?1")
    List<Integer> findAllIDsByEventId(int eventId);
}
