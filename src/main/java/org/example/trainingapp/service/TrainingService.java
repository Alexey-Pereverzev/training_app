package org.example.trainingapp.service;

import org.example.trainingapp.entity.Training;

import java.util.List;

public interface TrainingService {
    void createTraining(Training training);
    Training getTraining(Long id);
    List<Training> getAllTrainings();
}
