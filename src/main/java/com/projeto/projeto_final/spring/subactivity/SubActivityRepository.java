package com.projeto.projeto_final.spring.subactivity;

import com.projeto.projeto_final.spring.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubActivityRepository extends JpaRepository<SubActivity, Integer> {
    @Query("SELECT sa FROM SubActivity sa WHERE sa.id = ?1")
    SubActivity findById(int id);

    @Query("SELECT sa FROM SubActivity sa WHERE sa.activity.board.event.id = ?1 AND sa.deletionStatus = 0 ORDER BY sa.id")
    List<SubActivity> findAllActiveByEventId(int eventId);

    @Query("SELECT sa.id FROM SubActivity sa WHERE sa.activity.board.event.id = ?1")
    List<Integer> findAllIDsByEventId(int eventId);
}
