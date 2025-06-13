package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dto.TraineeDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.util.CredentialsUtil;
import org.example.trainingapp.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger log = Logger.getLogger(TraineeServiceImpl.class.getName());
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final Converter converter;

    @Autowired
    public TraineeServiceImpl(TraineeDao traineeDao, TrainerDao trainerDao, Converter converter) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.converter = converter;
    }


    @Override
    public void createTrainee(TraineeDto traineeDto) {
        ValidationUtils.validateTrainee(traineeDto);
        Trainee trainee = converter.dtoToEntity(traineeDto);

        List<String> existingUsernames = traineeDao.findAll().stream()
                .map(Trainee::getUsername)
                .toList();
        String generatedUsername = CredentialsUtil.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                existingUsernames
        );
        String password = CredentialsUtil.generatePassword(10);

        trainee.setUsername(generatedUsername);
        trainee.setPassword(password);
        trainee.setActive(true);
        traineeDao.save(trainee);
        log.info("Trainee created: " + trainee.getUsername());
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void updateTrainee(String username, String password, TraineeDto traineeDto) {
        ValidationUtils.validateTrainee(traineeDto);
        Trainee existing = getTrainee(traineeDto.getId());
        existing.setFirstName(traineeDto.getFirstName());
        existing.setLastName(traineeDto.getLastName());
        existing.setAddress(traineeDto.getAddress());
        existing.setDateOfBirth(traineeDto.getDateOfBirth());
        existing.setActive(traineeDto.isActive());
        traineeDao.update(existing);
        log.info("Trainee updated: " + existing.getId());
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void deleteTrainee(String username, String password, Long id) {
        Trainee trainee = getTrainee(id);
        traineeDao.deleteById(trainee.getId());
        log.info("Trainee deleted: " + id);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void deleteTraineeByUsername(String username, String password) {
        Trainee trainee = getTrainee(username);
        traineeDao.deleteById(trainee.getId());
        log.info("Trainee deleted by username: " + username);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void changeTraineePassword(String username, String password, Long id, String newPassword) {
        Trainee trainee = getTrainee(id);
        trainee.setPassword(newPassword);
        traineeDao.update(trainee);
        log.info("Trainee password updated: " + trainee.getId());
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void setTraineeActiveStatus(String username, String password, Long id, boolean isActive) {
        Trainee trainee = getTrainee(id);
        trainee.setActive(isActive);
        traineeDao.update(trainee);
        log.info("Trainee active status changed: " + trainee.getId() + " to " + isActive);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public void updateTraineeTrainers(String username, String password, List<Long> trainerIds) {
        Trainee trainee = getTrainee(username);
        List<Trainer> updatedTrainers = trainerIds.stream()     // getting new trainers
                .map(id -> trainerDao.findById(id).orElseThrow(() -> {
                    log.warning("Trainer not found for update: ID=" + id);
                    return new RuntimeException("Trainer not found with ID: " + id);
                }))
                .toList();
        trainee.setTrainers(updatedTrainers);
        traineeDao.update(trainee);
        log.info("Trainee trainers updated for: " + username);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE, Role.TRAINER}, checkOwnership = false)
    public TraineeDto getTrainee(String username, String password, Long id) {
        Trainee trainee = getTrainee(id);
        log.info("Retrieved Trainee: ID=" + id);
        return converter.entityToDto(trainee);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public TraineeDto getTraineeByUsername(String username, String password) {
        Trainee trainee = getTrainee(username);
        log.info("Retrieved Trainee by username: " + username);
        return converter.entityToDto(trainee);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER}, checkOwnership = false)
    public List<TraineeDto> getAllTrainees(String username, String password) {
        log.info("Retrieved all trainees requested by: " + username);
        return traineeDao.findAll().stream().map(converter::entityToDto).toList();
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public List<TrainerDto> getAvailableTrainersForTrainee(String username, String password) {
        Optional<Trainee> traineeOpt = traineeDao.findByUsername(username);
        if (traineeOpt.isEmpty()) {
            log.warning("No Trainee found for username: " + username);
            return Collections.emptyList();
        }
        Trainee trainee = traineeOpt.get();
        Set<Long> assignedTrainerIds = trainee.getTrainers().stream()
                .map(User::getId)
                .collect(Collectors.toSet());                           //  collecting assigned trainers' id
        List<Trainer> trainers = trainerDao.findAll().stream()          //  select only NOT assigned trainers
                .filter(trainer -> !assignedTrainerIds.contains(trainer.getId()))
                .toList();
        log.info("Retrieved available trainers for Trainee: " + username);
        return trainers.stream().map(converter::entityToDto).toList();
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINEE})
    public List<TrainingDto> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                                 String trainerName, String trainingTypeName) {
        Optional<Trainee> traineeOpt = traineeDao.findByUsernameWithTrainings(username);
        if (traineeOpt.isEmpty()) {
            log.warning("No Trainee found for username: " + username);
            return Collections.emptyList();
        }
        Trainee trainee = traineeOpt.get();
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
        log.info("Retrieved trainings for Trainee: " + username + " (filters: fromDate=" + fromDate +
                ", toDate=" + toDate + ", trainerName=" + trainerName + ", trainingTypeName=" + trainingTypeName + ")");
        return trainings.stream().map(converter::entityToDto).toList();
    }


    private Trainee getTrainee(Long id) {
        return traineeDao.findById(id).orElseThrow(() -> {
            log.warning("Trainee not found: ID=" + id);
            return new RuntimeException("Trainee not found with ID: " + id);
        });
    }

    private Trainee getTrainee(String username) {
        return traineeDao.findByUsername(username).orElseThrow(() -> {
            log.warning("Trainee not found: " + username);
            return new RuntimeException("Trainee not found: " + username);
        });
    }
}
