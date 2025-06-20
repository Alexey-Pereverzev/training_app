package org.example.trainingapp.service;

import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingDto;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {
    CredentialsDto createTrainee(TraineeRegisterDto traineeRegisterDto);
    TraineeResponseDto updateTrainee(String username, String password, TraineeRequestDto traineeRequestDto);
    void deleteTrainee(String username, String password);
    TraineeResponseDto getTrainee(String username, String password, Long id);
    List<TraineeRequestDto> getAllTrainees(String username, String password);
    TraineeResponseDto getTraineeByUsername(String username, String password);
    void changeTraineePassword(String username, String password, Long id, String newPassword);
    void setTraineeActiveStatus(String username, String password, Long id, boolean isActive);
    void deleteTraineeByUsername(String username, String password);
    List<TrainingDto> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                          String trainerName, String trainingTypeName);
    void updateTraineeTrainers(String username, String password, List<Long> trainerIds);
    List<TrainerDto> getAvailableTrainersForTrainee(String username, String password);
    List<TrainerShortDto> getTrainersForTrainee(String username, String password);
    void setNewPassword(String username, String oldPassword, String newPassword);
}
