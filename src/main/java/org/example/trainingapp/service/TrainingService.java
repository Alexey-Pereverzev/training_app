package org.example.trainingapp.service;

import org.example.trainingapp.dto.TrainingDto;

import java.util.List;

public interface TrainingService {
    void createTraining(String username, String password, TrainingDto trainingDto);
    TrainingDto getTraining(String username, String password, Long id);
    List<TrainingDto> getAllTrainings(String username, String password);
}
