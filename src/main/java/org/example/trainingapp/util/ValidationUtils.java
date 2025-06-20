package org.example.trainingapp.util;

import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainingDto;

import java.util.logging.Logger;

public class ValidationUtils {

    private static final Logger logger = Logger.getLogger(ValidationUtils.class.getName());
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void validateTrainee(TraineeRegisterDto traineeDto) {
        if (traineeDto.getFirstName() == null || traineeDto.getFirstName().isBlank()) {
            logger.severe("Validation failed: First name is missing");
            throw new IllegalArgumentException("First name is required.");
        }
        if (traineeDto.getLastName() == null || traineeDto.getLastName().isBlank()) {
            logger.severe("Validation failed: Last name is missing");
            throw new IllegalArgumentException("Last name is required.");
        }
    }

    public static void validateTrainee(TraineeRequestDto traineeRequestDto) {
        if (traineeRequestDto.getFirstName() == null || traineeRequestDto.getFirstName().isBlank()) {
            logger.severe("Validation failed: First name is missing");
            throw new IllegalArgumentException("First name is required.");
        }
        if (traineeRequestDto.getLastName() == null || traineeRequestDto.getLastName().isBlank()) {
            logger.severe("Validation failed: Last name is missing");
            throw new IllegalArgumentException("Last name is required.");
        }
        if (traineeRequestDto.getUsername() == null || traineeRequestDto.getUsername().isBlank()) {
            logger.severe("Validation failed: Username is missing");
            throw new IllegalArgumentException("Username is required.");
        }
        if (traineeRequestDto.getActive() == null) {
            logger.severe("Validation failed: Active status is missing");
            throw new IllegalArgumentException("Active status is required.");
        }

    }

    public static void validateTrainer(TrainerDto trainerDto) {
        if (trainerDto.getFirstName() == null || trainerDto.getFirstName().isBlank()) {
            logger.severe("Validation failed: First name is missing");
            throw new IllegalArgumentException("First name is required.");
        }
        if (trainerDto.getLastName() == null || trainerDto.getLastName().isBlank()) {
            logger.severe("Validation failed: Last name is missing");
            throw new IllegalArgumentException("Last name is required.");
        }
        if (trainerDto.getSpecializationName() == null || trainerDto.getSpecializationName().isBlank()) {
            logger.severe("Validation failed: Specialization is missing");
            throw new IllegalArgumentException("Specialization is required.");
        }
    }

    public static void validateTrainer(TrainerRegisterDto trainerDto) {
        if (trainerDto.getFirstName() == null || trainerDto.getFirstName().isBlank()) {
            logger.severe("Validation failed: First name is missing");
            throw new IllegalArgumentException("First name is required.");
        }
        if (trainerDto.getLastName() == null || trainerDto.getLastName().isBlank()) {
            logger.severe("Validation failed: Last name is missing");
            throw new IllegalArgumentException("Last name is required.");
        }
        if (trainerDto.getSpecializationName() == null || trainerDto.getSpecializationName().isBlank()) {
            logger.severe("Validation failed: Specialization is missing");
            throw new IllegalArgumentException("Specialization is required.");
        }
    }

    public static void validateTraining(TrainingDto trainingDto) {
        if (trainingDto.getTrainingName() == null || trainingDto.getTrainingName().isBlank()) {
            logger.severe("Validation failed: Training name is missing");
            throw new IllegalArgumentException("Training name is required.");
        }
        if (trainingDto.getTrainingDuration() == null || trainingDto.getTrainingDuration() <= 0) {
            logger.severe("Validation failed: Training duration must be positive");
            throw new IllegalArgumentException("Training duration must be positive.");
        }
        if (trainingDto.getTrainingDate() == null) {
            logger.severe("Validation failed: Training date is missing");
            throw new IllegalArgumentException("Training date is required.");
        }
        if (trainingDto.getTrainingType() == null || trainingDto.getTrainingType().isBlank()) {
            logger.severe("Validation failed: Training type is missing");
            throw new IllegalArgumentException("Training type is required.");
        }
        if (trainingDto.getTraineeId() == null) {
            logger.severe("Validation failed: Trainee is missing");
            throw new IllegalArgumentException("Trainee id is required.");
        }
        if (trainingDto.getTrainerId() == null) {
            logger.severe("Validation failed: Trainer is missing");
            throw new IllegalArgumentException("Trainer id is required.");
        }
    }

    public static void validateCredentials(CredentialsDto credentialsDto) {
        if (credentialsDto.getUsername() == null || credentialsDto.getUsername().isBlank()) {
            logger.severe("Validation failed: Username is missing");
            throw new IllegalArgumentException("Username is required.");
        }
        if (credentialsDto.getPassword() == null || credentialsDto.getPassword().isBlank()) {
            logger.severe("Validation failed: Password is missing");
            throw new IllegalArgumentException("Password is required.");
        }
    }

    public static void validateCredentials(ChangePasswordDto changePasswordDto) {
        if (changePasswordDto.getUsername() == null || changePasswordDto.getUsername().isBlank()) {
            logger.severe("Validation failed: Username is missing");
            throw new IllegalArgumentException("Username is required.");
        }
        if (changePasswordDto.getOldPassword() == null || changePasswordDto.getOldPassword().isBlank()) {
            logger.severe("Validation failed: Old password is missing");
            throw new IllegalArgumentException("Old password is required.");
        }
        if (changePasswordDto.getNewPassword() == null || changePasswordDto.getNewPassword().isBlank()) {
            logger.severe("Validation failed: New password is missing");
            throw new IllegalArgumentException("New password is required.");
        }
    }

    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            logger.severe("Validation failed: Username is missing");
            throw new IllegalArgumentException("Username is required.");
        }
    }
}

