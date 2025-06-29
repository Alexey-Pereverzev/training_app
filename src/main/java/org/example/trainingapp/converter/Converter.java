package org.example.trainingapp.converter;

import org.apache.commons.lang3.EnumUtils;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TraineeShortDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.dto.TrainingTypeDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.TrainingTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class Converter {
    private static final Logger log = LoggerFactory.getLogger(Converter.class.getName());
    private final TrainingTypeDao trainingTypeDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    @Autowired
    public Converter(TrainingTypeDao trainingTypeDao, TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainingTypeDao = trainingTypeDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }


    public Trainee dtoToEntity(TraineeRegisterDto traineeRegisterDto) {
        return Trainee.builder()
                .firstName(traineeRegisterDto.getFirstName())
                .lastName(traineeRegisterDto.getLastName())
                .address(traineeRegisterDto.getAddress())
                .dateOfBirth(traineeRegisterDto.getDateOfBirth())
                .build();
    }

    public Trainer dtoToEntity(TrainerRegisterDto dto) {
        TrainingType type = resolveAndValidateTrainingType(dto.getSpecializationName());
        return Trainer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .specialization(type)
                .build();
    }

    public TrainerResponseDto entityToDtoWithoutUsername(Trainer trainer, List<TraineeShortDto> trainees) {
        return TrainerResponseDto.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .active(trainer.isActive())
                .specializationName(trainer.getSpecialization().getName())
                .trainees(trainees)
                .build();
    }

    public TrainerResponseDto entityToResponseDto(Trainer trainer, List<TraineeShortDto> trainees) {
        TrainerResponseDto trainerResponseDto = entityToDtoWithoutUsername(trainer, trainees);
        trainerResponseDto.setUsername(trainer.getUsername());
        return trainerResponseDto;
    }

    public TraineeResponseDto entityToDtoWithoutUsername(Trainee trainee, List<TrainerShortDto> trainerDtos) {
        return TraineeResponseDto.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .active(trainee.isActive())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .trainers(trainerDtos)
                .build();
    }

    public TraineeResponseDto entityToResponseDto(Trainee trainee, List<TrainerShortDto> trainers) {
        TraineeResponseDto traineeResponseDto = entityToDtoWithoutUsername(trainee, trainers);
        traineeResponseDto.setUsername(trainee.getUsername());
        return traineeResponseDto;
    }

    public TraineeShortDto entityToShortDto(Trainee trainee) {
        return TraineeShortDto.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .build();
    }

    public TrainerShortDto entityToShortDto(Trainer trainer) {
        return TrainerShortDto.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .specializationName(trainer.getSpecialization().getName())
                .build();
    }

    public Training dtoToEntity(TrainingRequestDto dto) {
        String trainerName = dto.getTrainerName();
        String traineeName = dto.getTraineeName();
        Trainer trainer = trainerDao.findByUsername(trainerName).orElseThrow(() -> {
            log.warn("Trainer not found: username={}", trainerName);
            return new RuntimeException("Not found trainer with username: " + trainerName);
        });
        TrainingType type = getTrainingTypeByName(trainer.getSpecialization().getName());
        Trainee trainee = traineeDao.findByUsername(traineeName).orElseThrow(() -> {
            log.warn("Trainee not found: username={}", traineeName);
            return new RuntimeException("Not found trainee with username: " + traineeName);
        });
        return Training.builder()
                .trainingName(dto.getName())
                .trainingDate(dto.getDate())
                .trainingDuration(dto.getDuration())
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(type)
                .build();
    }

    public TrainingResponseDto entityToDtoWithNullTrainer(Training training) {
        return TrainingResponseDto.builder()
                .name(training.getTrainingName())
                .type(training.getTrainingType() != null ? training.getTrainingType().getName() : null)
                .date(training.getTrainingDate())
                .duration(training.getTrainingDuration())
                .traineeName(training.getTrainee() != null ? training.getTrainee().getUsername() : null)
                .build();
    }

    public TrainingResponseDto entityToDtoWithNullTrainee(Training training) {
        return TrainingResponseDto.builder()
                .name(training.getTrainingName())
                .type(training.getTrainingType() != null ? training.getTrainingType().getName() : null)
                .date(training.getTrainingDate())
                .duration(training.getTrainingDuration())
                .trainerName(training.getTrainer() != null ? training.getTrainer().getUsername() : null)
                .build();
    }

    public TrainingType getTrainingTypeByName(String trainingTypeName) {
        return resolveAndValidateTrainingType(trainingTypeName);
    }

    public TrainingTypeDto entityToDto(TrainingType trainingType) {
        return TrainingTypeDto.builder()
                .trainingTypeId(trainingType.getId())
                .trainingTypeName(trainingType.getName())
                .build();
    }

    private TrainingType resolveAndValidateTrainingType(String typeName) {
        if (!EnumUtils.isValidEnum(TrainingTypeEnum.class, typeName.toUpperCase())) {
            log.error("TrainingType '{}' is not supported.", typeName);
            throw new RuntimeException("TrainingType '" + typeName + "' is not supported.");
        }
        return trainingTypeDao.findByName(typeName)
                .orElseThrow(() -> {
                    log.warn("TrainingType not found: {}", typeName);
                    return new RuntimeException("TrainingType not found: " + typeName);
                });
    }

}
