package com.projeto.projeto_final.spring.config.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitializationStatusRepository extends JpaRepository<InitializationStatus, Integer> {

}
