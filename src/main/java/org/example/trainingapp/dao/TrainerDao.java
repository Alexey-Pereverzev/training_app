package org.example.trainingapp.dao;

import org.example.trainingapp.entity.Trainer;

import java.util.Optional;

public interface TrainerDao extends CrudDao<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    Optional<Trainer> findByUsernameWithTrainings( String username);
    Optional<Trainer> findByUsernameWithTrainees(String username);
}
