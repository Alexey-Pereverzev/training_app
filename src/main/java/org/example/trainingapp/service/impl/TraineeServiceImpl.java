package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.exception.ForbiddenAccessException;
import org.example.trainingapp.metrics.RegistrationMetrics;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.UserRepository;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.util.AuthContextUtil;
import org.example.trainingapp.util.CredentialsUtil;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;


@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class.getName());
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final Converter converter;
    private final AuthContextUtil authContextUtil;
    private final RegistrationMetrics registrationMetrics;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
                              UserRepository userRepository, Converter converter, AuthContextUtil authContextUtil,
                              RegistrationMetrics registrationMetrics) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.converter = converter;
        this.authContextUtil = authContextUtil;
        this.registrationMetrics = registrationMetrics;
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
        trainee.setPassword(password);
        trainee.setActive(true);
        traineeRepository.save(trainee);
        log.info("Trainee created: {}", trainee.getUsername());
        registrationMetrics.incrementTrainee();
        return CredentialsDto.builder()
                .username(trainee.getUsername())
                .password(trainee.getPassword())
                .build();
    }


    @Override
    @Transactional
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public TraineeResponseDto updateTrainee(TraineeRequestDto traineeRequestDto) {
        ValidationUtils.validateTrainee(traineeRequestDto);
        String username = traineeRequestDto.getUsername();
        Trainee existing = traineeRepository.findByUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    log.warn("Trainee '{}' not found during updating ", username);
                    return new RuntimeException("Not found trainee with username: " + username);
                });
        String address = traineeRequestDto.getAddress();
        if (address!=null && !address.isBlank()) {
            existing.setAddress(address);
        }
        if (traineeRequestDto.getDateOfBirth()!=null) {
            existing.setDateOfBirth(traineeRequestDto.getDateOfBirth());
        }
        existing.setActive(traineeRequestDto.getActive());
        traineeRepository.save(existing);
        log.info("Trainee updated: {}", existing.getId());
        List<TrainerShortDto> trainers = getTrainersForTrainee(username);
        return converter.entityToResponseDto(existing, trainers);           //  with username
    }


    @Override
    @Transactional
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void deleteTrainee(String username) {
        if (!username.equals(authContextUtil.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            traineeRepository.deleteByUsername(username);
            log.info("Trainee deleted: {}", username);
        }
    }


    @Override
    @Transactional
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public Boolean setTraineeActiveStatus(ActiveStatusDto activeStatusDto) {
        ValidationUtils.validateActiveStatus(activeStatusDto);
        Trainee trainee = getTrainee(activeStatusDto.getUsername());
        Boolean active = activeStatusDto.getActive();
        trainee.setActive(active);
        traineeRepository.save(trainee);
        log.info("Trainee active status changed: {} to {}", trainee.getId(), active);
        return active;
    }


    @Override
    @Transactional
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public List<TrainerShortDto> updateTraineeTrainers(UpdateTrainerListDto updateTrainerListDto) {
        ValidationUtils.validateUpdateTrainerList(updateTrainerListDto);
        String username = updateTrainerListDto.getUsername();
        Trainee trainee = traineeRepository.findByUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    log.warn("Trainee '{}' not found during updating trainers", username);
                    return new RuntimeException("Not found trainee with username: " + username);
                });

        if (trainee.getTrainers() != null) {            // delete trainee from old trainers
            for (Trainer oldTr : new ArrayList<>(trainee.getTrainers())) {
                Trainer fullOld = trainerRepository.findByUsernameWithTrainees(oldTr.getUsername())
                        .orElseThrow(() -> new RuntimeException(
                                "Trainer not found: " + oldTr.getUsername()));
                fullOld.getTrainees().remove(trainee);
                trainerRepository.save(fullOld);
            }
            trainee.getTrainers().clear();
        }

        List<Trainer> updatedTrainers = updateTrainerListDto.getTrainerUsernames().stream()
                .map(u -> trainerRepository.findByUsernameWithTrainees(u).orElseThrow(() -> new RuntimeException(
                                "Not found trainer with username: " + u)))
                .peek(trainer -> {
                    if (!trainer.getTrainees().contains(trainee)) {         //  add trainee to new trainers
                        trainer.getTrainees().add(trainee);
                    }
                    trainerRepository.save(trainer);
                })
                .collect(Collectors.toCollection(ArrayList::new));          //  must be modifiable list here because .peek() is a lazy operation
        trainee.setTrainers(updatedTrainers);
        traineeRepository.save(trainee);
        log.info("Trainee trainers updated for: {}", username);
        return updatedTrainers.stream().map(converter::entityToShortDto).toList();
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public TraineeResponseDto getTraineeByUsername(String username) {
        if (!username.equals(authContextUtil.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainee trainee = getTrainee(username);
            List<TrainerShortDto> trainers = getTrainersForTrainee(username);
            log.info("Retrieved Trainee by username: {}", username);
            return converter.entityToDtoWithoutUsername(trainee, trainers);
        }
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public List<TrainerShortDto> getAvailableTrainersForTrainee(String username) {
        if (!username.equals(authContextUtil.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainee trainee = traineeRepository.findByUsernameWithTrainers(username)
                    .orElseThrow(() -> {
                        log.warn("Trainee '{}' not found during getting not assigned trainers", username);
                        return new RuntimeException("Not found trainee with username: " + username);
                    });
            Set<Long> assignedTrainerIds = trainee.getTrainers().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());                           //  collecting assigned trainers' id
            List<Trainer> trainers = trainerRepository.findAll().stream()          //  select only NOT assigned trainers
                    .filter(trainer -> !assignedTrainerIds.contains(trainer.getId()))
                    .toList();
            log.info("Retrieved available trainers for Trainee: {}", username);
            return trainers.stream().map(converter::entityToShortDto).toList();
        }
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public List<TrainingResponseDto> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                                         String trainerName, String trainingTypeName) {
        if (!username.equals(authContextUtil.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainee trainee = traineeRepository.findByUsernameWithTrainings(username)
                    .orElseThrow(() -> {
                        log.warn("Trainee '{}' not found during getting trainings", username);
                        return new RuntimeException("Not found trainee with username: " + username);
                    });
            List<Training> trainings = trainee.getTrainings().stream()
                    .filter(training -> {
                        boolean dateMatch = (fromDate == null || !training.getTrainingDate().isBefore(fromDate)) &&
                                (toDate == null || !training.getTrainingDate().isAfter(toDate));
                        boolean trainerMatch = (trainerName == null ||
                                training.getTrainer().getUsername().equalsIgnoreCase(trainerName));
                        boolean typeMatch = (trainingTypeName == null ||
                                training.getTrainingType().getName().equalsIgnoreCase(trainingTypeName));
                        return dateMatch && trainerMatch && typeMatch;
                    })      //  3 filter conditions here - on date period, on trainer name and on training type
                    .toList();
            log.info("Retrieved trainings for Trainee: {} (filters: fromDate={}, toDate={}, trainerName={}, trainingTypeName={})", username, fromDate, toDate, trainerName, trainingTypeName);
            return trainings.stream().map(converter::entityToDtoWithNullTrainee).toList();      //  trainee name not included
        }
    }


    @Override
    @Transactional
    public void setNewPassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = getTrainee(username);
        trainee.setPassword(newPassword);
        traineeRepository.save(trainee);
        log.info("Password updated for trainee {}", username);
    }


    private List<TrainerShortDto> getTrainersForTrainee(String username) {
        Trainee trainee = traineeRepository.findByUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    log.warn("Trainee '{}' not found during getting trainers", username);
                    return new RuntimeException("Not found trainee with username: " + username);
                });
        List<Trainer> trainers = trainee.getTrainers();
        log.info("Retrieved all trainers for Trainee: {}", username);
        return trainers.stream().map(converter::entityToShortDto).toList();
    }


    private Trainee getTrainee(String username) {
        return traineeRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainee not found: {}", username);
            return new RuntimeException("Trainee not found: " + username);
        });
    }

}
