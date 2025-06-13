package org.example.trainingapp.service;

import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {
    void createTrainer(TrainerDto trainerDto);
    void updateTrainer(String username, String password, TrainerDto trainerDto);
    TrainerDto getTrainer(String username, String password, Long id);
    List<TrainerDto> getAllTrainers(String username, String password);
    TrainerDto getTrainerByUsername(String username, String password);
    void changeTrainerPassword(String username, String password, Long id, String newPassword);
    void setTrainerActiveStatus(String username, String password, Long id, boolean isActive);
    List<TrainingDto> getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                          String traineeName);
}
