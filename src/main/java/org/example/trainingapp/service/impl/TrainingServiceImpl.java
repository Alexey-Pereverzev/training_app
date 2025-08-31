package org.example.trainingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ActionType;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.dto.TrainingUpdateRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class.getName());
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final Converter converter;
    private final TrainingExecutionMetrics trainingExecutionMetrics;
    private final TrainerHoursPublisher trainerHoursPublisher;


    @Override
    @Transactional
    public String createTraining(TrainingRequestDto trainingRequestDto) {
        ValidationUtils.validateTraining(trainingRequestDto);
        Training training;
        try {
            training = converter.dtoToEntity(trainingRequestDto);
        } catch (EntityNotFoundException e) {
            log.warn("Failed to convert TrainingRequestDto to Training: {}", e.getMessage(), e);
            throw new NoSuchElementException(e);
        }
        String trainerUsername = training.getTrainer().getUsername();
        String traineeUsername = training.getTrainee().getUsername();
        Trainer trainer = trainerRepository.findByUsernameWithTrainees(trainerUsername)
                .orElseThrow(() -> {
                    log.warn("Trainer '{}' not found during creating training ", trainerUsername);
                    return new NoSuchElementException("Not found trainer with username: " + trainerUsername);
                });
        Trainee trainee = traineeRepository.findByUsernameWithTrainers(traineeUsername)
                .orElseThrow(() -> {
                    log.warn("Trainee '{}' not found during creating training ", traineeUsername);
                    return new NoSuchElementException("Not found trainee with username: " + traineeUsername);
                });
        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
        }
        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
        }
        String newTrainingName = generateUniqueTrainingName(trainingRequestDto.getName(), training.getTrainingDate(),
                trainerUsername);
        training.setTrainingName(newTrainingName);

        trainingExecutionMetrics.record(() -> {     //  write Prometheus metric
            trainingRepository.save(training);
        });
        trainerRepository.save(trainer);             // updates both trainer and trainee

        // notification of 2nd microservice
        TrainingUpdateRequest update = converter.trainingAndActionToUpdateRequest(training, ActionType.ADD);
        trainerHoursPublisher.publishUpdate(update);

        log.info("Training created: {}", training.getTrainingName());
        return "Training " + training.getTrainingName() + " with id " + training.getId() + " created successfully";
    }


    private String generateUniqueTrainingName(String baseName, LocalDate date, String trainerUsername) {
        String datePrefix = date + "#";
        List<Training> existingTrainings = trainingRepository
                .findByTrainer_UsernameAndTrainingDate(trainerUsername, date);
        int maxIndex = existingTrainings.stream()
                .map(Training::getTrainingName)
                .map(name -> {                          // Parsing index: "2024-08-01#3 - Power Yoga"
                    try {
                        String indexPart = name.substring(datePrefix.length(), name.indexOf(" - ")).trim();
                        return Integer.parseInt(indexPart);
                    } catch (Exception e) {
                        log.warn("Failed to parse index from training name: {}", name, e);
                        return 0;                       // invalid index - return 0
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);                        // if there are no records at all or all errors - start from 0
        return date + "#" + (maxIndex + 1) + " - " + baseName;
    }


    @Override
    @Transactional
    public void deleteTrainingByName(String trainingName) {
        Training training = trainingRepository.findByTrainingName(trainingName)
                .orElseThrow(() -> {
                    log.warn("Training not found: {}", trainingName);
                    return new NoSuchElementException("Training not found: " + trainingName);
                });
        LocalDate today = LocalDate.now();
        if (training.getTrainingDate().isBefore(today)) {              // past training - prohibit to delete
            log.warn("Deleting past trainings prohibited.");
            throw new IllegalStateException("Deleting past trainings prohibited.");
        }

        trainingRepository.delete(training);
        TrainingUpdateRequest updateRequest = converter.trainingAndActionToUpdateRequest(training, ActionType.DELETE);
        trainerHoursPublisher.publishUpdate(updateRequest);

        log.info("Training deleted: {} by trainer {}", trainingName, training.getTrainer().getUsername());
    }


}

