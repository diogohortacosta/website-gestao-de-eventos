package com.projeto.projeto_final.spring.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event e WHERE e.id = ?1")
    Event findById(int id);

    @Query("SELECT e FROM Event e WHERE e.startDate = ?1")
    Event findByStartDate(LocalDateTime startDate);

    @Query("SELECT e FROM Event e WHERE e.endDate = ?1")
    Event findByEndDate(LocalDateTime endDate);

    @Query("SELECT e FROM Event e JOIN e.user u WHERE u.username = ?1 AND e.deletionStatus = 0")
    List<Event> findByUsername(String username);

    @Query("SELECT e FROM Event e WHERE e.deletionStatus = 0")
    List<Event> findAllActive();
}
