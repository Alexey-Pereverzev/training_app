package org.example.trainingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.TraineeShortDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.exception.ForbiddenAccessException;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.util.AuthContextUtil;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class.getName());
    private final TrainerRepository trainerRepository;
    private final Converter converter;
    private final TrainingTypeRepository trainingTypeRepository;
    private final AuthContextUtil authContextUtil;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public TrainerResponseDto updateTrainer(TrainerRequestDto trainerRequestDto) {
        ValidationUtils.validateTrainer(trainerRequestDto);
        String username = trainerRequestDto.getUsername();
        Trainer existing = trainerRepository.findByUsernameWithTrainees(username)
                .orElseThrow(() -> {
                    log.warn("Trainer '{}' not found during updaing ", username);
                    return new NoSuchElementException("Not found trainer with username: " + username);
                });
        String specialization = trainerRequestDto.getSpecializationName();
        TrainingType type = trainingTypeRepository.findByName(specialization)
                .orElseThrow(() -> {
                    log.warn("TrainingType not found: {}", specialization);
                    return new NoSuchElementException("TrainingType not found: " + specialization);
                });
        //  checking type validity and catching the exception
        if (!ValidationUtils.isValidTrainingTypeEnum(specialization.toUpperCase())) {
            log.error("Unsupported TrainingType: {}", specialization);
            throw new NoSuchElementException("TrainingType '" + specialization + "' is not supported.");
        }
        existing.setSpecialization(type);
        existing.setActive(trainerRequestDto.getActive());
        trainerRepository.save(existing);
        log.info("Trainer updated: {}", existing.getId());
        List<TraineeShortDto> trainees = getTraineesForTrainer(username);
        return converter.entityToResponseDto(existing, trainees);
    }


    @Override
    public TrainerResponseDto getTrainerByUsername(String username) {
        if (!username.equals(authContextUtil.getUsername())) {
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
    @Transactional
    public Boolean setTrainerActiveStatus(ActiveStatusDto activeStatusDto) {
        ValidationUtils.validateActiveStatus(activeStatusDto);
        Trainer trainer = getTrainer(activeStatusDto.getUsername());
        Boolean active = activeStatusDto.getActive();
        trainer.setActive(active);
        trainerRepository.save(trainer);
        log.info("Trainer active status changed: {} to {}", trainer.getId(), active);
        return active;
    }


    @Override
    public List<TrainingResponseDto> getTrainerTrainings(String username, LocalDate fromDate,
                                                         LocalDate toDate, String traineeName) {
        if (!username.equals(authContextUtil.getUsername())) {
            throw new ForbiddenAccessException("User is not the owner of entity");
        } else {
            ValidationUtils.validateUsername(username);
            Trainer trainer = trainerRepository.findByUsernameWithTrainings(username)
                    .orElseThrow(() -> {
                        log.warn("Trainer '{}' not found during getting trainings", username);
                        return new NoSuchElementException("Not found trainer with username: " + username);
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
    @Transactional
    public void setNewPassword(String username, String newPassword) {
        Trainer trainer = getTrainer(username);
        trainer.setPassword(passwordEncoder.encode(newPassword));
        trainerRepository.save(trainer);
        log.info("Password updated for trainer {}", username);
    }


    private List<TraineeShortDto> getTraineesForTrainer(String username) {
        Trainer trainer = trainerRepository.findByUsernameWithTrainees(username)
                .orElseThrow(() -> {
                    log.warn("Trainer '{}' not found during getting trainees", username);
                    return new NoSuchElementException("Not found trainer with username: " + username);
                });
        List<Trainee> trainees = trainer.getTrainees();
        log.info("Retrieved available trainees for Trainer: {}", username);
        return trainees.stream().map(converter::entityToShortDto).toList();
    }


    private Trainer getTrainer(String username) {
        return trainerRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("Trainer not found: {}", username);
            return new NoSuchElementException("Trainer not found: " + username);
        });
    }

}
