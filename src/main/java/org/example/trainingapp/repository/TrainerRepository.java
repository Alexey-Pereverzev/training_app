package org.example.trainingapp.repository;

import org.example.trainingapp.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUsername(String username);

    @Query("SELECT DISTINCT t FROM Trainer t LEFT JOIN FETCH t.trainings tr LEFT JOIN FETCH tr.trainee " +
            "LEFT JOIN FETCH tr.trainingType WHERE t.username = :username")
    Optional<Trainer> findByUsernameWithTrainings(@Param("username") String username);

    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.trainees WHERE t.username = :username")
    Optional<Trainer> findByUsernameWithTrainees(@Param("username") String username);
}

