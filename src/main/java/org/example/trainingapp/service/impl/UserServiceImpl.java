package org.example.trainingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.metrics.RegistrationMetrics;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.UserRepository;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.service.UserService;
import org.example.trainingapp.util.CredentialsUtil;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class.getName());
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final AuthenticationService authenticationService;
    private final RegistrationMetrics registrationMetrics;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final Converter converter;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public CredentialsDto createTrainer(TrainerRegisterDto trainerRegisterDto) {
        ValidationUtils.validateTrainer(trainerRegisterDto);
        Trainer trainer;
        try {
            trainer = converter.dtoToEntity(trainerRegisterDto);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.warn("Failed to convert TrainerRegisterDto to Trainer: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        Set<String> existingUsernames = userRepository.findUsernamesByFirstNameAndLastName(trainer.getFirstName(),
                trainer.getLastName());
        String generatedUsername = CredentialsUtil.generateUsername(trainer.getFirstName(), trainer.getLastName(),
                existingUsernames);
        String password = CredentialsUtil.generatePassword(10);
        trainer.setUsername(generatedUsername);
        trainer.setPassword(password);
        trainer.setPassword(passwordEncoder.encode(password));
        trainer.setActive(true);
        trainerRepository.save(trainer);
        log.info("Trainer created: {}", trainer.getUsername());
        registrationMetrics.incrementTrainer();         //  add to Prometheus metric
        return CredentialsDto.builder()
                .username(trainer.getUsername())
                .password(password)
                .build();
    }


    @Override
    @Transactional
    public CredentialsDto createTrainee(TraineeRegisterDto traineeRegisterDto) {
        ValidationUtils.validateTrainee(traineeRegisterDto);
        Trainee trainee = converter.dtoToEntity(traineeRegisterDto);
        Set<String> existingUsernames = userRepository.findUsernamesByFirstNameAndLastName(trainee.getFirstName(),
                trainee.getLastName());
        String generatedUsername = CredentialsUtil.generateUsername(trainee.getFirstName(), trainee.getLastName(),
                existingUsernames);
        String password = CredentialsUtil.generatePassword(10);
        trainee.setUsername(generatedUsername);
        trainee.setPassword(passwordEncoder.encode(password));
        trainee.setActive(true);
        traineeRepository.save(trainee);
        log.info("Trainee created: {}", trainee.getUsername());
        registrationMetrics.incrementTrainee();         //  add to Prometheus metric
        return CredentialsDto.builder()
                .username(trainee.getUsername())
                .password(password)
                .build();
    }


    @Override
    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto) {
        ValidationUtils.validateCredentials(changePasswordDto);
        User user = userRepository.findByUsername(changePasswordDto.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + changePasswordDto.getUsername()));
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            log.error("Validation failed: Invalid old password");
            throw new IllegalArgumentException("Invalid old password");
        }
        Role role = authenticationService.getRole(changePasswordDto.getUsername(), changePasswordDto.getOldPassword());
        if (role==Role.TRAINER) {
            trainerService.setNewPassword(changePasswordDto.getUsername(), changePasswordDto.getNewPassword());
        } else {
            traineeService.setNewPassword(changePasswordDto.getUsername(), changePasswordDto.getNewPassword());
        }
        log.info("Password changed for user: {}", changePasswordDto.getUsername());
    }
}
