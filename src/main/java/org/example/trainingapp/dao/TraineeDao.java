package org.example.trainingapp.dao;

import org.example.trainingapp.entity.Trainee;

import java.util.Optional;

public interface TraineeDao extends CrudDao<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
    Optional<Trainee> findByUsernameWithTrainings(String username);
    void deleteByUsername(String username);
    Optional<Trainee> findByUsernameWithTrainers(String username);

}