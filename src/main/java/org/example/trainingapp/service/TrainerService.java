package org.example.trainingapp.service;

import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.dto.TrainingResponseDto;

import java.time.LocalDate;
import java.util.List;


public interface TrainerService {
    TrainerResponseDto updateTrainer(TrainerRequestDto trainerRequestDto);
    TrainerResponseDto getTrainerByUsername(String username);
    Boolean setTrainerActiveStatus(ActiveStatusDto activeStatusDto);
    List<TrainingResponseDto> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName);
    void setNewPassword(String username, String newPassword);
}
