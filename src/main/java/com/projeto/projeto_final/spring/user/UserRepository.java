package com.projeto.projeto_final.spring.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.id = ?1")
    User findById(int id);

    @Query("SELECT u FROM User u ORDER BY u.name")
    List<User> findAllOrderByName();
}
