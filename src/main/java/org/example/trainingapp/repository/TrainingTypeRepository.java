package org.example.trainingapp.repository;

import org.example.trainingapp.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findByName(String name);
}
