package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.service.TrainingService;
import org.example.trainingapp.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class.getName());
    private final TrainingDao trainingDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final Converter converter;

    @Autowired
    public TrainingServiceImpl(TrainingDao trainingDao, TrainerDao trainerDao, TraineeDao traineeDao, Converter converter) {
        this.trainingDao = trainingDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
        this.converter = converter;
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER})
    public String createTraining(String authHeader, TrainingRequestDto trainingRequestDto) {
        ValidationUtils.validateTraining(trainingRequestDto);
        Training training = converter.dtoToEntity(trainingRequestDto);
        Trainer trainer = trainerDao.findByUsernameWithTrainees(training.getTrainer().getUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found: " + training.getTrainer().getUsername()));
        Trainee trainee = traineeDao.findByUsernameWithTrainers(training.getTrainee().getUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found: " + training.getTrainee().getUsername()));
        if (!trainer.getTrainees().contains(trainee)) {
            trainer.getTrainees().add(trainee);
        }
        if (!trainee.getTrainers().contains(trainer)) {
            trainee.getTrainers().add(trainer);
        }
        trainingDao.save(training);
        trainerDao.update(trainer);             // updates both trainer and trainee
        log.info("Training created: {}", training.getTrainingName());
        return training.getTrainingName();
    }

}