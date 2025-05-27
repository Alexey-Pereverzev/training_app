package org.example.trainingapp.service;

import org.example.trainingapp.entity.Trainee;

import java.util.List;

public interface TraineeService {
    void createTrainee(Trainee trainee);
    void updateTrainee(Trainee trainee);
    void deleteTrainee(Long id);
    Trainee getTrainee(Long id);
    List<Trainee> getAllTrainees();
}
