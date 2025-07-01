package org.example.trainingapp.util;

import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class.getName());
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }


    public static void validateTrainee(TraineeRegisterDto traineeDto) {
        if (traineeDto.getFirstName() == null || traineeDto.getFirstName().isBlank()) {
            logger.error("Validation failed: First name is missing for Trainee registration");
            throw new IllegalArgumentException("First name is required.");
        }
        if (traineeDto.getLastName() == null || traineeDto.getLastName().isBlank()) {
            logger.error("Validation failed: Last name is missing for Trainee registration");
            throw new IllegalArgumentException("Last name is required.");
        }
    }


    public static void validateTrainee(TraineeRequestDto traineeRequestDto) {
        if (traineeRequestDto.getFirstName() == null || traineeRequestDto.getFirstName().isBlank()) {
            logger.error("Validation failed: First name is missing for Trainee");
            throw new IllegalArgumentException("First name is required.");
        }
        if (traineeRequestDto.getLastName() == null || traineeRequestDto.getLastName().isBlank()) {
            logger.error("Validation failed: Last name is missing for Trainee");
            throw new IllegalArgumentException("Last name is required.");
        }
        if (traineeRequestDto.getUsername() == null || traineeRequestDto.getUsername().isBlank()) {
            logger.error("Validation failed: Username is missing for Trainee");
            throw new IllegalArgumentException("Username is required.");
        }
        if (traineeRequestDto.getActive() == null) {
            logger.error("Validation failed: Active status is missing for Trainee");
            throw new IllegalArgumentException("Active status is required.");
        }
        String expectedPrefix = traineeRequestDto.getFirstName() + "." + traineeRequestDto.getLastName();
        if (!traineeRequestDto.getUsername().startsWith(expectedPrefix)) {
            logger.error("Validation failed for trainee: Username '{}' must start with '{}'",
                    traineeRequestDto.getUsername(), expectedPrefix);
            throw new IllegalArgumentException("Username must start with '" + expectedPrefix + "'.");
        }
    }


    public static void validateTrainer(TrainerRequestDto trainerRequestDto) {
        if (trainerRequestDto.getFirstName() == null || trainerRequestDto.getFirstName().isBlank()) {
            logger.error("Validation failed: First name is missing for Trainer");
            throw new IllegalArgumentException("First name is required.");
        }
        if (trainerRequestDto.getLastName() == null || trainerRequestDto.getLastName().isBlank()) {
            logger.error("Validation failed: Last name is missing for Trainer");
            throw new IllegalArgumentException("Last name is required.");
        }
        if (trainerRequestDto.getSpecializationName() == null || trainerRequestDto.getSpecializationName().isBlank()) {
            logger.error("Validation failed: Specialization is missing for Trainer");
            throw new IllegalArgumentException("Specialization is required.");
        }
        if (trainerRequestDto.getUsername() == null || trainerRequestDto.getUsername().isBlank()) {
            logger.error("Validation failed: Username is missing for Trainer");
            throw new IllegalArgumentException("Username is required.");
        }
        if (trainerRequestDto.getActive() == null) {
            logger.error("Validation failed: Active status is missing for Trainer");
            throw new IllegalArgumentException("Active status is required.");
        }
        String expectedPrefix = trainerRequestDto.getFirstName() + "." + trainerRequestDto.getLastName();
        if (!trainerRequestDto.getUsername().startsWith(expectedPrefix)) {
            logger.error("Validation failed for trainer: Username '{}' must start with '{}'",
                    trainerRequestDto.getUsername(), expectedPrefix);
            throw new IllegalArgumentException("Username must start with '" + expectedPrefix + "'.");
        }
    }


    public static void validateTrainer(TrainerRegisterDto trainerDto) {
        if (trainerDto.getFirstName() == null || trainerDto.getFirstName().isBlank()) {
            logger.error("Validation failed: First name is missing for Trainer registration");
            throw new IllegalArgumentException("First name is required.");
        }
        if (trainerDto.getLastName() == null || trainerDto.getLastName().isBlank()) {
            logger.error("Validation failed: Last name is missing for Trainer registration");
            throw new IllegalArgumentException("Last name is required.");
        }
        if (trainerDto.getSpecializationName() == null || trainerDto.getSpecializationName().isBlank()) {
            logger.error("Validation failed: Specialization is missing for Trainer registration");
            throw new IllegalArgumentException("Specialization is required.");
        }
    }


    public static void validateTraining(TrainingRequestDto trainingRequestDto) {
        if (trainingRequestDto.getName() == null || trainingRequestDto.getName().isBlank()) {
            logger.error("Validation failed: Training name is missing");
            throw new IllegalArgumentException("Training name is required.");
        }
        if (trainingRequestDto.getDuration() == null || trainingRequestDto.getDuration() <= 0) {
            logger.error("Validation failed: Training duration must be positive");
            throw new IllegalArgumentException("Training duration must be positive.");
        }
        if (trainingRequestDto.getDate() == null) {
            logger.error("Validation failed: Training date is missing");
            throw new IllegalArgumentException("Training date is required.");
        }
        if (trainingRequestDto.getTraineeName() == null || trainingRequestDto.getTraineeName().isBlank()) {
            logger.error("Validation failed: Trainee username is missing");
            throw new IllegalArgumentException("Trainee username is required.");
        }
        if (trainingRequestDto.getTrainerName() == null|| trainingRequestDto.getTrainerName().isBlank()) {
            logger.error("Validation failed: Trainer username is missing");
            throw new IllegalArgumentException("Trainer username is required.");
        }
    }


    public static void validateCredentials(CredentialsDto credentialsDto) {
        if (credentialsDto.getUsername() == null || credentialsDto.getUsername().isBlank()) {
            logger.error("Validation failed: Username is missing in credentials");
            throw new IllegalArgumentException("Username is required.");
        }
        if (credentialsDto.getPassword() == null || credentialsDto.getPassword().isBlank()) {
            logger.error("Validation failed: Password is missing in credentials");
            throw new IllegalArgumentException("Password is required.");
        }
    }


    public static void validateCredentials(ChangePasswordDto changePasswordDto) {
        if (changePasswordDto.getUsername() == null || changePasswordDto.getUsername().isBlank()) {
            logger.error("Validation failed: Username is missing for changing password");
            throw new IllegalArgumentException("Username is required.");
        }
        if (changePasswordDto.getOldPassword() == null || changePasswordDto.getOldPassword().isBlank()) {
            logger.error("Validation failed: Old password is missing");
            throw new IllegalArgumentException("Old password is required.");
        }
        if (changePasswordDto.getNewPassword() == null || changePasswordDto.getNewPassword().isBlank()) {
            logger.error("Validation failed: New password is missing");
            throw new IllegalArgumentException("New password is required.");
        }
    }


    public static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            logger.error("Validation failed: Username is missing");
            throw new IllegalArgumentException("Username is required.");
        }
    }


    public static void validateUpdateTrainerList(UpdateTrainerListDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            logger.error("Validation failed: Username is missing for updating trainers list");
            throw new IllegalArgumentException("Username is required.");
        }

        if (dto.getTrainerUsernames() == null || dto.getTrainerUsernames().isEmpty()) {
            logger.error("Validation failed: Trainer list is empty or null");
            throw new IllegalArgumentException("At least one trainer must be provided.");
        }

        for (String trainerUsername : dto.getTrainerUsernames()) {
            if (trainerUsername == null || trainerUsername.isBlank()) {
                logger.error("Validation failed: Trainer username is null or blank");
                throw new IllegalArgumentException("Trainer usernames must not be null or blank.");
            }
        }
    }


    public static void validateActiveStatus(ActiveStatusDto activeStatusDto) {
        if (activeStatusDto.getUsername() == null || activeStatusDto.getUsername().isBlank()) {
            logger.error("Validation failed: Username is missing for changing active status");
            throw new IllegalArgumentException("Username is required.");
        }
        if (activeStatusDto.getActive() == null) {
            logger.error("Validation failed: Active status is missing");
            throw new IllegalArgumentException("Active status is required.");
        }
    }

}

