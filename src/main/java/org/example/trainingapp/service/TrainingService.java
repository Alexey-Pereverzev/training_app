package org.example.trainingapp.service;

import org.example.trainingapp.dto.TrainingRequestDto;


public interface TrainingService {
    String createTraining(TrainingRequestDto trainingRequestDto);
    void deleteTrainingByName(String trainingName);
}
