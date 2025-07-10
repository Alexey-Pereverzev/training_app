package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class DaoAuthenticationService implements AuthenticationService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private static final Logger logger = LoggerFactory.getLogger(DaoAuthenticationService.class.getName());

    @Autowired
    public DaoAuthenticationService(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }


    @Override
    public Role authorize(String username, String password) {
        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);
        if (trainerOpt.isPresent() && trainerOpt.get().getPassword().equals(password)) {
            logger.info("Trainer authentication successful: {}", username);
            return Role.TRAINER;
        } else {
            logger.warn("Trainer authentication failed (wrong password): {}", username);
        }
        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
        if (traineeOpt.isPresent() && traineeOpt.get().getPassword().equals(password)) {
            logger.info("Trainee authentication successful: {}", username);
            return Role.TRAINEE;
        } else {
            logger.warn("Trainee authentication failed (wrong password): {}", username);
        }
        logger.error("Authentication failed: No user found with username '{}'", username);
        throw new SecurityException("Invalid credentials");
    }


    @Override
    public Role authorize(CredentialsDto credentialsDto) {
        ValidationUtils.validateCredentials(credentialsDto);
        return authorize(credentialsDto.getUsername(), credentialsDto.getPassword());
    }

}
