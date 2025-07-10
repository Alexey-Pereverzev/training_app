package org.example.trainingapp.repository;

import org.example.trainingapp.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUsername(String username);

    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainings tr LEFT JOIN FETCH tr.trainer " +
            "LEFT JOIN FETCH tr.trainingType WHERE t.username = :username")
    Optional<Trainee> findByUsernameWithTrainings(@Param("username") String username);

    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username")
    Optional<Trainee> findByUsernameWithTrainers(@Param("username") String username);

    void deleteByUsername(String username);
}

