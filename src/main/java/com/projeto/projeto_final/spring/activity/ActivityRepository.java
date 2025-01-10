package com.projeto.projeto_final.spring.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Query("SELECT a FROM Activity a WHERE a.id = ?1")
    Activity findById(int id);

    @Query("SELECT a FROM Activity a WHERE a.board.event.id = ?1 AND a.deletionStatus = 0 ORDER BY a.board.id")
    List<Activity> findAllActiveByEventId(int eventId);

    @Query("SELECT a FROM Activity a WHERE a.board.id = ?1")
    List<Activity> findByBoardId(int boardId);

    @Query("SELECT a.id FROM Activity a WHERE a.board.event.id = ?1")
    List<Integer> findAllIDsByEventId(int eventId);
}
