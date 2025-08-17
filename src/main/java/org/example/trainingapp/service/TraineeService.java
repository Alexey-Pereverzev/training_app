package org.example.trainingapp.service;

import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;

import java.time.LocalDate;
import java.util.List;


public interface TraineeService {
    TraineeResponseDto updateTrainee(TraineeRequestDto traineeRequestDto);
    void deleteTrainee(String username);
    TraineeResponseDto getTraineeByUsername(String username);
    Boolean setTraineeActiveStatus(ActiveStatusDto activeStatusDto);
    List<TrainingResponseDto> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                                  String trainerName, String trainingTypeName);
    List<TrainerShortDto> updateTraineeTrainers(UpdateTrainerListDto updateTrainerListDto);
    List<TrainerShortDto> getAvailableTrainersForTrainee(String username);
    void setNewPassword(String username, String newPassword);
}
