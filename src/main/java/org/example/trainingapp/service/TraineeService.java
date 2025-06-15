package org.example.trainingapp.service;

import org.example.trainingapp.dto.TraineeDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {
    void createTrainee(TraineeDto traineeDto);
    void updateTrainee(String username, String password, TraineeDto traineeDto);
    void deleteTrainee(String username, String password, Long id);
    TraineeDto getTrainee(String username, String password, Long id);
    List<TraineeDto> getAllTrainees(String username, String password);
    TraineeDto getTraineeByUsername(String username, String password);
    void changeTraineePassword(String username, String password, Long id, String newPassword);
    void setTraineeActiveStatus(String username, String password, Long id, boolean isActive);
    void deleteTraineeByUsername(String username, String password);
    List<TrainingDto> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                          String trainerName, String trainingTypeName);
    void updateTraineeTrainers(String username, String password, List<Long> trainerIds);
    List<TrainerDto> getAvailableTrainersForTrainee(String username, String password);
}
