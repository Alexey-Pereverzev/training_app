package org.example.trainingapp.dao;

import org.example.trainingapp.entity.TrainingType;

import java.util.Optional;

public interface TrainingTypeDao extends CrudDao<TrainingType, Long> {
    Optional<TrainingType> findByName(String name);
}
