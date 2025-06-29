package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.UserDao;
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
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.util.AuthUtil;
import org.example.trainingapp.util.CredentialsUtil;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;


@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class.getName());
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final UserDao userDao;
    private final Converter converter;

    @Autowired
    public TraineeServiceImpl(TraineeDao traineeDao, TrainerDao trainerDao, UserDao userDao, Converter converter) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.userDao = userDao;
        this.converter = converter;
    }


    @Override
    public CredentialsDto createTrainee(TraineeRegisterDto traineeRegisterDto) {
        ValidationUtils.validateTrainee(traineeRegisterDto);
        Trainee trainee = converter.dtoToEntity(traineeRegisterDto);
        long count = userDao.countUsersByNameAndSurname(
                trainee.getFirstName(), trainee.getLastName());
        String generatedUsername = CredentialsUtil.generateUsername(
                trainee.getFirstName(), trainee.getLastName(), count);
        String password = CredentialsUtil.generatePassword(10);
        trainee.setUsername(generatedUsername);
        trainee.setPassword(password);
        trainee.setActive(true);
        traineeDao.save(trainee);
        log.info("Trainee created: {}", trainee.getUsername());
        return CredentialsDto.builder()
                .username(trainee.getUsername())
                .password(trainee.getPassword())
                .build();
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public TraineeResponseDto updateTrainee(String authHeader, TraineeRequestDto traineeRequestDto) {
        ValidationUtils.validateTrainee(traineeRequestDto);
        String username = traineeRequestDto.getUsername();
        Trainee existing = traineeDao.findByUsernameWithTrainers(username)
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
        traineeDao.update(existing);
        log.info("Trainee updated: {}", existing.getId());
        List<TrainerShortDto> trainers = getTrainersForTrainee(username);
        return converter.entityToResponseDto(existing, trainers);           //  with username
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void deleteTrainee(String authHeader, String username) {
        CredentialsDto credentialsDto = AuthUtil.decodeBasicAuth(authHeader);
        if (!username.equals(credentialsDto.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            traineeDao.deleteByUsername(username);
            log.info("Trainee deleted: {}", username);
        }
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public Boolean setTraineeActiveStatus(String authHeader, ActiveStatusDto activeStatusDto) {
        ValidationUtils.validateActiveStatus(activeStatusDto);
        Trainee trainee = getTrainee(activeStatusDto.getUsername());
        Boolean active = activeStatusDto.getActive();
        trainee.setActive(active);
        traineeDao.update(trainee);
        log.info("Trainee active status changed: {} to {}", trainee.getId(), active);
        return active;
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public List<TrainerShortDto> updateTraineeTrainers(String authHeader, UpdateTrainerListDto updateTrainerListDto) {
        ValidationUtils.validateUpdateTrainerList(updateTrainerListDto);
        String username = updateTrainerListDto.getUsername();
        Trainee trainee = traineeDao.findByUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    log.warn("Trainee '{}' not found during updating trainers", username);
                    return new RuntimeException("Not found trainee with username: " + username);
                });

        if (trainee.getTrainers() != null) {            // delete trainee from old trainers
            for (Trainer oldTr : new ArrayList<>(trainee.getTrainers())) {
                Trainer fullOld = trainerDao.findByUsernameWithTrainees(oldTr.getUsername())
                        .orElseThrow(() -> new RuntimeException(
                                "Trainer not found: " + oldTr.getUsername()));
                fullOld.getTrainees().remove(trainee);
                trainerDao.update(fullOld);
            }
            trainee.getTrainers().clear();
        }

        List<Trainer> updatedTrainers = updateTrainerListDto.getTrainerUsernames().stream()
                .map(u -> trainerDao.findByUsernameWithTrainees(u).orElseThrow(() -> new RuntimeException(
                                "Not found trainer with username: " + u)))
                .peek(trainer -> {
                    if (!trainer.getTrainees().contains(trainee)) {         // add trainee to new trainers
                        trainer.getTrainees().add(trainee);
                    }
                    trainerDao.update(trainer);
                })
                .toList();
        trainee.setTrainers(updatedTrainers);
        traineeDao.update(trainee);
        log.info("Trainee trainers updated for: {}", username);
        return updatedTrainers.stream().map(converter::entityToShortDto).toList();
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public TraineeResponseDto getTraineeByUsername(String authHeader, String username) {
        CredentialsDto credentialsDto = AuthUtil.decodeBasicAuth(authHeader);
        if (!username.equals(credentialsDto.getUsername())) {
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
    public List<TrainerShortDto> getAvailableTrainersForTrainee(String authHeader, String username) {
        CredentialsDto credentialsDto = AuthUtil.decodeBasicAuth(authHeader);
        if (!username.equals(credentialsDto.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainee trainee = traineeDao.findByUsernameWithTrainers(username)
                    .orElseThrow(() -> {
                        log.warn("Trainee '{}' not found during getting not assigned trainers", username);
                        return new RuntimeException("Not found trainee with username: " + username);
                    });
            Set<Long> assignedTrainerIds = trainee.getTrainers().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());                           //  collecting assigned trainers' id
            List<Trainer> trainers = trainerDao.findAll().stream()          //  select only NOT assigned trainers
                    .filter(trainer -> !assignedTrainerIds.contains(trainer.getId()))
                    .toList();
            log.info("Retrieved available trainers for Trainee: {}", username);
            return trainers.stream().map(converter::entityToShortDto).toList();
        }
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public List<TrainingResponseDto> getTraineeTrainings(String authHeader, String username, LocalDate fromDate,
                                                         LocalDate toDate, String trainerName, String trainingTypeName) {
        CredentialsDto credentialsDto = AuthUtil.decodeBasicAuth(authHeader);
        if (!username.equals(credentialsDto.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainee trainee = traineeDao.findByUsernameWithTrainings(username)
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
    public void setNewPassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = getTrainee(username);
        trainee.setPassword(newPassword);
        traineeDao.update(trainee);
        log.info("Password updated for trainee {}", username);
    }


    private List<TrainerShortDto> getTrainersForTrainee(String username) {
        Trainee trainee = traineeDao.findByUsernameWithTrainers(username)
                .orElseThrow(() -> {
                    log.warn("Trainee '{}' not found during getting trainers", username);
                    return new RuntimeException("Not found trainee with username: " + username);
                });
        List<Trainer> trainers = trainee.getTrainers();
        log.info("Retrieved all trainers for Trainee: {}", username);
        return trainers.stream().map(converter::entityToShortDto).toList();
    }


    private Trainee getTrainee(String username) {
        return traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainee not found: {}", username);
            return new RuntimeException("Trainee not found: " + username);
        });
    }

}
