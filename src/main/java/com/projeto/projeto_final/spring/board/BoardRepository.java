package com.projeto.projeto_final.spring.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    @Query("SELECT b FROM Board b WHERE b.id = ?1")
    Board findById(int id);

    @Query("SELECT b FROM Board b WHERE b.event.id = ?1 ORDER BY b.position")
    List<Board> findAllByEventId(int eventId);

    @Query("SELECT b FROM Board b WHERE b.event.id = ?1 AND b.name = ?2")
    Board findByEventIdName(int eventId, String name);

    @Query("SELECT b.id FROM Board b WHERE b.event.id = ?1")
    List<Integer> findAllIDsByEventId(int eventId);
}
