package org.example.trainingapp.service.impl;

import org.apache.commons.lang3.EnumUtils;
import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dao.UserDao;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeShortDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.TrainingTypeEnum;
import org.example.trainingapp.exception.ForbiddenAccessException;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.util.AuthUtil;
import org.example.trainingapp.util.CredentialsUtil;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class.getName());
    private final TrainerDao trainerDao;
    private final Converter converter;
    private final TrainingTypeDao trainingTypeDao;
    private final UserDao userDao;

    @Autowired
    public TrainerServiceImpl(TrainerDao trainerDao, Converter converter,
                              TrainingTypeDao trainingTypeDao, UserDao userDao) {
        this.trainerDao = trainerDao;
        this.converter = converter;
        this.trainingTypeDao = trainingTypeDao;
        this.userDao = userDao;
    }


    @Override
    public CredentialsDto createTrainer(TrainerRegisterDto trainerRegisterDto) {
        ValidationUtils.validateTrainer(trainerRegisterDto);
        Trainer trainer = converter.dtoToEntity(trainerRegisterDto);
        long count = userDao.countUsersByNameAndSurname(
                trainer.getFirstName(), trainer.getLastName());
        String generatedUsername = CredentialsUtil.generateUsername(
                trainer.getFirstName(), trainer.getLastName(), count);
        String password = CredentialsUtil.generatePassword(10);
        trainer.setUsername(generatedUsername);
        trainer.setPassword(password);
        trainer.setActive(true);
        trainerDao.save(trainer);
        log.info("Trainer created: {}", trainer.getUsername());
        return CredentialsDto.builder()
                .username(trainer.getUsername())
                .password(trainer.getPassword())
                .build();
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public TrainerResponseDto updateTrainer(String authHeader, TrainerRequestDto trainerRequestDto) {
        ValidationUtils.validateTrainer(trainerRequestDto);
        String username = trainerRequestDto.getUsername();
        Trainer existing = trainerDao.findByUsernameWithTrainees(username)
                .orElseThrow(() -> {
                    log.warn("Trainer '{}' not found during updaing ", username);
                    return new RuntimeException("Not found trainer with username: " + username);
                });
        String specialization = trainerRequestDto.getSpecializationName();
        TrainingType type = trainingTypeDao.findByName(specialization)
                .orElseThrow(() -> {
                    log.warn("TrainingType not found: {}", specialization);
                    return new RuntimeException("TrainingType not found: " + specialization);
                });
        //  checking type validity and catching the exception
        if (!EnumUtils.isValidEnum(TrainingTypeEnum.class, specialization.toUpperCase())) {
            log.error("Unsupported TrainingType: {}", specialization);
            throw new RuntimeException("TrainingType '" + specialization + "' is not supported.");
        }
        existing.setSpecialization(type);
        existing.setActive(trainerRequestDto.getActive());
        trainerDao.update(existing);
        log.info("Trainer updated: {}", existing.getId());
        List<TraineeShortDto> trainees = getTraineesForTrainer(username);
        return converter.entityToResponseDto(existing, trainees);
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public TrainerResponseDto getTrainerByUsername(String authHeader, String username) {
        CredentialsDto credentialsDto = AuthUtil.decodeBasicAuth(authHeader);
        if (!username.equals(credentialsDto.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainer trainer = getTrainer(username);
            List<TraineeShortDto> trainees = getTraineesForTrainer(username);
            log.info("Retrieved Trainer by username: {}", username);
            return converter.entityToDtoWithoutUsername(trainer, trainees);     //  dto without username
        }
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public Boolean setTrainerActiveStatus(String authHeader, ActiveStatusDto activeStatusDto) {
        ValidationUtils.validateActiveStatus(activeStatusDto);
        Trainer trainer = getTrainer(activeStatusDto.getUsername());
        Boolean active = activeStatusDto.getActive();
        trainer.setActive(active);
        trainerDao.update(trainer);
        log.info("Trainer active status changed: {} to {}", trainer.getId(), active);
        return active;
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public List<TrainingResponseDto> getTrainerTrainings(String authHeader, String username, LocalDate fromDate,
                                                         LocalDate toDate, String traineeName) {
        CredentialsDto credentialsDto = AuthUtil.decodeBasicAuth(authHeader);
        if (!username.equals(credentialsDto.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainer trainer = trainerDao.findByUsernameWithTrainings(username)
                    .orElseThrow(() -> {
                        log.warn("Trainer '{}' not found during getting trainings", username);
                        return new RuntimeException("Not found trainer with username: " + username);
                    });
            List<Training> trainings = trainer.getTrainings().stream()
                    .filter(training -> {
                        boolean dateMatch = (fromDate == null || !training.getTrainingDate().isBefore(fromDate)) &&
                                (toDate == null || !training.getTrainingDate().isAfter(toDate));
                        boolean traineeMatch = (traineeName == null ||
                                training.getTrainee().getUsername().equalsIgnoreCase(traineeName));
                        return dateMatch && traineeMatch;
                    })          //  2 filter conditions here - on date period and on trainee name
                    .toList();
            log.info("Retrieved trainings for Trainer: {} (filters: fromDate={}, toDate={}, traineeName={})", username,
                    fromDate, toDate, traineeName);
            return trainings.stream().map(converter::entityToDtoWithNullTrainer).toList();  //  trainer name not included
        }
    }


    @Override
    public void setNewPassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = getTrainer(username);
        trainer.setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Password updated for trainer {}", username);
    }


    private List<TraineeShortDto> getTraineesForTrainer(String username) {
        Trainer trainer = trainerDao.findByUsernameWithTrainees(username)
                .orElseThrow(() -> {
                    log.warn("Trainer '{}' not found during getting trainees", username);
                    return new RuntimeException("Not found trainer with username: " + username);
                });
        List<Trainee> trainees = trainer.getTrainees();
        log.info("Retrieved available trainees for Trainer: {}", username);
        return trainees.stream().map(converter::entityToShortDto).toList();
    }


    private Trainer getTrainer(String username) {
        return trainerDao.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainer not found: {}", username);
            return new RuntimeException("Trainer not found: " + username);
        });
    }

}
