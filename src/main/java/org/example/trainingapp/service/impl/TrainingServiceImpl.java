package org.example.trainingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.metrics.TrainingExecutionMetrics;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingRepository;
import org.example.trainingapp.service.TrainingService;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class.getName());
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final Converter converter;
    private final TrainingExecutionMetrics trainingExecutionMetrics;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainerRepository trainerRepository,
                               TraineeRepository traineeRepository, Converter converter,
                               TrainingExecutionMetrics trainingExecutionMetrics) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.converter = converter;
        this.trainingExecutionMetrics = trainingExecutionMetrics;
    }


    @Override
    @Transactional
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public String createTraining(TrainingRequestDto trainingRequestDto) {
        ValidationUtils.validateTraining(trainingRequestDto);
        Training training;
        try {
            training = converter.dtoToEntity(trainingRequestDto);
        } catch (EntityNotFoundException e) {
            log.warn("Failed to convert TrainingRequestDto to Training: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        String trainerUsername = training.getTrainer().getUsername();
        String traineeUsername = training.getTrainee().getUsername();
        Trainer trainer = trainerRepository.findByUsernameWithTrainees(trainerUsername)
                .orElseThrow(() -> {
                    log.warn("Trainer '{}' not found during creating training ", trainerUsername);
                    return new RuntimeException("Not found trainer with username: " + trainerUsername);
                });
        Trainee trainee = traineeRepository.findByUsernameWithTrainers(traineeUsername)
                .orElseThrow(() -> {
                    log.warn("Trainee '{}' not found during creating training ", traineeUsername);
                    return new RuntimeException("Not found trainee with username: " + traineeUsername);
                });
        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
        }
        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
        }
        trainingExecutionMetrics.record(() -> {     //  write Prometheus metric
            trainingRepository.save(training);
        });
        trainerRepository.save(trainer);             // updates both trainer and trainee
        log.info("Training created: {}", training.getTrainingName());
        return training.getTrainingName();
    }

}

