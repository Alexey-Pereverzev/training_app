package org.example.trainingapp.converter;

import org.apache.commons.lang3.EnumUtils;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dto.TraineeDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.TrainingTypeEnum;
import org.example.trainingapp.service.impl.TrainerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;


@Component
public class Converter {
    private static final Logger log = Logger.getLogger(TrainerServiceImpl.class.getName());
    private final TrainingTypeDao trainingTypeDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    @Autowired
    public Converter(TrainingTypeDao trainingTypeDao, TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainingTypeDao = trainingTypeDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }


    public Trainee dtoToEntity(TraineeDto traineeDto) {
        return Trainee.builder()
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .active(traineeDto.isActive())
                .address(traineeDto.getAddress())
                .dateOfBirth(traineeDto.getDateOfBirth())
                .username(traineeDto.getUsername())
                .build();
    }


    public TraineeDto entityToDto(Trainee trainee) {
        return TraineeDto.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .active(trainee.isActive())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .username(trainee.getUsername())
                .build();
    }

    public Trainer dtoToEntity(TrainerDto trainerDto) {
        String specialization = trainerDto.getSpecializationName();
        TrainingType type = trainingTypeDao.findByName(specialization)
                .orElseThrow(() -> {
                    log.warning("TrainingType not found: " + specialization);
                    return new RuntimeException("TrainingType not found: " + specialization);
                });
        //  checking type validity and catching the exception
        if (!EnumUtils.isValidEnum(TrainingTypeEnum.class, specialization.toUpperCase())) {
            log.severe("TrainingType '" + specialization + "' is not supported.");
            throw new RuntimeException("TrainingType '" + specialization + "' is not supported.");
        }
        return Trainer.builder()
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .active(trainerDto.isActive())
                .specialization(type)
                .username(trainerDto.getUsername())
                .build();
    }

    public TrainerDto entityToDto(Trainer trainer) {
        return TrainerDto.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .active(trainer.isActive())
                .specializationName(trainer.getSpecialization().getName())
                .username(trainer.getUsername())
                .build();
    }

    public Training dtoToEntity(TrainingDto dto) {
        TrainingType type = getTrainingTypeByName(dto.getTrainingType());
        Long trainerId = dto.getTrainerId();
        Long traineeId = dto.getTraineeId();
        Trainer trainer = trainerDao.findById(trainerId).orElseThrow(() -> {
            log.warning("Trainer not found: ID=" + trainerId);
            return new RuntimeException("Trainer not found with ID: " + trainerId);
        });
        Trainee trainee = traineeDao.findById(traineeId).orElseThrow(() -> {
            log.warning("Trainee not found: ID=" + traineeId);
            return new RuntimeException("Trainee not found with ID: " + traineeId);
        });
        return Training.builder()
                .trainingName(dto.getTrainingName())
                .trainingDate(dto.getTrainingDate())
                .trainingDuration(dto.getTrainingDuration())
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(type)
                .build();
    }

    public TrainingDto entityToDto(Training training) {
        return TrainingDto.builder()
                .id(training.getId())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType() != null ? training.getTrainingType().getName() : null)
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .traineeId(training.getTrainee() != null ? training.getTrainee().getId() : null)
                .trainerId(training.getTrainer() != null ? training.getTrainer().getId() : null)
                .build();
    }

    public TrainingType getTrainingTypeByName(String trainyngTypeName) {
        TrainingType trainingType = trainingTypeDao.findByName(trainyngTypeName)
                .orElseThrow(() -> {
                    log.warning("TrainingType not found: " + trainyngTypeName);
                    return new RuntimeException("TrainingType not found: " + trainyngTypeName);
                });
        // checking type is valid for enum
        if (!EnumUtils.isValidEnum(TrainingTypeEnum.class, trainyngTypeName.toUpperCase())) {
            log.severe("TrainingType '" + trainyngTypeName + "' is not supported.");
            throw new RuntimeException("TrainingType '" + trainyngTypeName + "' is not supported.");
        }
        return trainingType;
    }
}
