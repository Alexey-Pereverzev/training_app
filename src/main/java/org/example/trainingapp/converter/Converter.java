package org.example.trainingapp.converter;

import org.apache.commons.lang3.EnumUtils;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TraineeShortDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.TrainingTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;


@Component
public class Converter {
    private static final Logger log = Logger.getLogger(Converter.class.getName());
    private final TrainingTypeDao trainingTypeDao;
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    @Autowired
    public Converter(TrainingTypeDao trainingTypeDao, TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainingTypeDao = trainingTypeDao;
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }


    public Trainee dtoToEntity(TraineeRequestDto traineeRequestDto) {
        return Trainee.builder()
                .firstName(traineeRequestDto.getFirstName())
                .lastName(traineeRequestDto.getLastName())
                .active(traineeRequestDto.isActive())
                .address(traineeRequestDto.getAddress())
                .dateOfBirth(traineeRequestDto.getDateOfBirth())
                .username(traineeRequestDto.getUsername())
                .build();
    }


    public Trainee dtoToEntity(TraineeRegisterDto traineeRegisterDto) {
        return Trainee.builder()
                .firstName(traineeRegisterDto.getFirstName())
                .lastName(traineeRegisterDto.getLastName())
                .address(traineeRegisterDto.getAddress())
                .dateOfBirth(traineeRegisterDto.getDateOfBirth())
                .build();
    }


    public TraineeRequestDto entityToDto(Trainee trainee) {
        return TraineeRequestDto.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .active(trainee.isActive())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .username(trainee.getUsername())
                .build();
    }

    public TraineeResponseDto entityToDtoWithoutUsername(Trainee trainee, List<TrainerDto> trainers) {
        List<TrainerShortDto> trainerDtos = trainers.stream().map(this::dtoToShortDto).toList();
        return TraineeResponseDto.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .active(trainee.isActive())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .trainers(trainerDtos)
                .build();
    }

    public TraineeResponseDto entityToDto(Trainee trainee, List<TrainerDto> trainers) {
        TraineeResponseDto traineeResponseDto = entityToDtoWithoutUsername(trainee, trainers);
        traineeResponseDto.setUsername(trainee.getUsername());
        return traineeResponseDto;
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


    public Trainer dtoToEntity(TrainerRegisterDto trainerRegisterDto) {
        String specialization = trainerRegisterDto.getSpecializationName();
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
                .firstName(trainerRegisterDto.getFirstName())
                .lastName(trainerRegisterDto.getLastName())
                .specialization(type)
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

    public TrainerResponseDto entityToResponseDto(Trainer trainer, List<TraineeShortDto> trainees) {
        return TrainerResponseDto.builder()
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .active(trainer.isActive())
                .specializationName(trainer.getSpecialization().getName())
                .trainees(trainees)
                .build();
    }

    public TraineeResponseDto entityToResponseDto(Trainee trainee, List<TrainerShortDto> trainers) {
        return TraineeResponseDto.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .active(trainee.isActive())
                .trainers(trainers)
                .build();
    }


    private TrainerShortDto dtoToShortDto(TrainerDto trainerDto) {
        return TrainerShortDto.builder()
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .specializationName(trainerDto.getSpecializationName())
                .username(trainerDto.getUsername())
                .build();
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

    public TrainingType getTrainingTypeByName(String trainingTypeName) {
        TrainingType trainingType = trainingTypeDao.findByName(trainingTypeName)
                .orElseThrow(() -> {
                    log.warning("TrainingType not found: " + trainingTypeName);
                    return new RuntimeException("TrainingType not found: " + trainingTypeName);
                });
        // checking type is valid for enum
        if (!EnumUtils.isValidEnum(TrainingTypeEnum.class, trainingTypeName.toUpperCase())) {
            log.severe("TrainingType '" + trainingTypeName + "' is not supported.");
            throw new RuntimeException("TrainingType '" + trainingTypeName + "' is not supported.");
        }
        return trainingType;
    }



}
