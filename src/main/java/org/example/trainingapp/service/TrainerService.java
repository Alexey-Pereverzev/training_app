package org.example.trainingapp.service;

import org.example.trainingapp.entity.Trainer;

import java.util.List;

public interface TrainerService {
    void createTrainer(Trainer trainer);
    void updateTrainer(Trainer trainer);
    Trainer getTrainer(Long id);
    List<Trainer> getAllTrainers();
}
