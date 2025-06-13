package org.example.trainingapp.service.impl;

import org.apache.commons.lang3.EnumUtils;
import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.TrainingTypeEnum;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.util.CredentialsUtil;
import org.example.trainingapp.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;


@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = Logger.getLogger(TrainerServiceImpl.class.getName());
    private final TrainerDao trainerDao;
    private final Converter converter;
    private final TrainingTypeDao trainingTypeDao;

    @Autowired
    public TrainerServiceImpl(TrainerDao trainerDao, Converter converter, TrainingTypeDao trainingTypeDao) {
        this.trainerDao = trainerDao;
        this.converter = converter;
        this.trainingTypeDao = trainingTypeDao;
    }


    @Override
    public void createTrainer(TrainerDto trainerDto) {
        ValidationUtils.validateTrainer(trainerDto);
        Trainer trainer = converter.dtoToEntity(trainerDto);

        List<String> existingUsernames = trainerDao.findAll().stream()
                .map(Trainer::getUsername)
                .toList();
        String username = CredentialsUtil.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                existingUsernames
        );
        String password = CredentialsUtil.generatePassword(10);

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);
        trainerDao.save(trainer);
        log.info("Trainer created: " + trainer.getUsername());
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public void updateTrainer(String username, String password, TrainerDto trainerDto) {
        ValidationUtils.validateTrainer(trainerDto);
        Trainer existing = getTrainer(trainerDto.getId());
        existing.setFirstName(trainerDto.getFirstName());
        existing.setLastName(trainerDto.getLastName());
        String specialization = trainerDto.getSpecializationName();
        TrainingType type = trainingTypeDao.findByName(specialization)
                .orElseThrow(() -> {
                    log.warning("TrainingType not found: " + specialization);
                    return new RuntimeException("TrainingType not found: " + specialization);
                });
        //  checking type validity and catching the exception
        if (!EnumUtils.isValidEnum(TrainingTypeEnum.class, specialization.toUpperCase())) {
            log.severe("Unsupported TrainingType: " + specialization);
            throw new RuntimeException("TrainingType '" + specialization + "' is not supported.");
        }
        existing.setSpecialization(type);
        existing.setActive(trainerDto.isActive());
        trainerDao.update(existing);
        log.info("Trainer updated: " + existing.getId());
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER}, checkOwnership = false)
    public TrainerDto getTrainer(String username, String password, Long id) {
        Trainer trainer = getTrainer(id);
        log.info("Retrieved Trainer: ID=" + id);
        return converter.entityToDto(trainer);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public TrainerDto getTrainerByUsername(String username, String password) {
        Trainer trainer = getTrainer(username);
        log.info("Retrieved Trainer by username: " + username);
        return converter.entityToDto(trainer);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER}, checkOwnership = false)
    public List<TrainerDto> getAllTrainers(String username, String password) {
        log.info("Retrieved all trainers requested by: " + username);
        return trainerDao.findAll().stream().map(converter::entityToDto).toList();
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public void changeTrainerPassword(String username, String password, Long id, String newPassword) {
        Trainer trainer = getTrainer(id);
        trainer.setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Trainer password updated: " + trainer.getId());
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public void setTrainerActiveStatus(String username, String password, Long id, boolean isActive) {
        Trainer trainer = getTrainer(id);
        trainer.setActive(isActive);
        trainerDao.update(trainer);
        log.info("Trainer active status changed: " + trainer.getId() + " to " + isActive);
    }

    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public List<TrainingDto> getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                                 String traineeName) {
        Trainer trainer = trainerDao.findByUsernameWithTrainings(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        List<Training> trainings = trainer.getTrainings().stream()
                .filter(training -> {
                    boolean dateMatch = (fromDate == null || !training.getTrainingDate().isBefore(fromDate)) &&
                            (toDate == null || !training.getTrainingDate().isAfter(toDate));
                    boolean traineeMatch = (traineeName == null ||
                            training.getTrainee().getUsername().equalsIgnoreCase(traineeName));
                    return dateMatch && traineeMatch;
                })          //  2 filter conditions here - on date period and on trainee name
                .toList();
        log.info("Retrieved trainings for Trainer: " + username + " (filters: fromDate=" + fromDate +
                ", toDate=" + toDate + ", traineeName=" + traineeName + ")");
        return trainings.stream().map(converter::entityToDto).toList();
    }


    private Trainer getTrainer(Long id) {
        return trainerDao.findById(id).orElseThrow(() -> {
            log.warning("Trainer not found: ID=" + id);
            return new RuntimeException("Trainer not found with ID: " + id);
        });
    }

    private Trainer getTrainer(String username) {
        return trainerDao.findByUsername(username).orElseThrow(() -> {
            log.warning("Trainer not found: " + username);
            return new RuntimeException("Trainer not found: " + username);
        });
    }

}
