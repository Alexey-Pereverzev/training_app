package org.example.trainingapp.service;

import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TrainerRegisterDto;


public interface UserService {
    void changePassword(ChangePasswordDto changePasswordDto);
    CredentialsDto createTrainee(TraineeRegisterDto traineeRegisterDto);
    CredentialsDto createTrainer(TrainerRegisterDto trainerRegisterDto);
}
