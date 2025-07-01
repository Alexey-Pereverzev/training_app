package org.example.trainingapp.service;

import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;

import java.time.LocalDate;
import java.util.List;


public interface TraineeService {
    CredentialsDto createTrainee(TraineeRegisterDto traineeRegisterDto);
    TraineeResponseDto updateTrainee(String authHeader, TraineeRequestDto traineeRequestDto);
    void deleteTrainee(String authHeader, String username);
    TraineeResponseDto getTraineeByUsername(String authHeader, String username);
    Boolean setTraineeActiveStatus(String authHeader, ActiveStatusDto activeStatusDto);
    List<TrainingResponseDto> getTraineeTrainings(String authHeader, String username, LocalDate fromDate, LocalDate toDate,
                                                  String trainerName, String trainingTypeName);
    List<TrainerShortDto> updateTraineeTrainers(String authHeader, UpdateTrainerListDto updateTrainerListDto);
    List<TrainerShortDto> getAvailableTrainersForTrainee(String authHeader, String username);
    void setNewPassword(String username, String oldPassword, String newPassword);
}
