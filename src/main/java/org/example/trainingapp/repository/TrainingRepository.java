package org.example.trainingapp.repository;

import org.example.trainingapp.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findByTrainer_UsernameAndTrainingDate(String trainerUsername, LocalDate trainingDate);
    boolean existsByTrainingName(String trainingName);
    Optional<Training> findByTrainingName(String trainingName);
    void deleteByTrainingName(String trainingName);
}

