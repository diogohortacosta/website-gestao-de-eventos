package com.projeto.projeto_final.spring.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);

    @Query("select r from Role r where r.id = ?1")
    Role findById(int id);

    @Query("select r from Role r join r.users u where u.username = ?1")
    List<Role> findByUsername(String username);
}
