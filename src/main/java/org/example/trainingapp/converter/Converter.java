package org.example.trainingapp.converter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingTypeRepository;

import org.example.trainingapp.util.ValidationUtils;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class Converter {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;


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
        Trainer trainer = trainerRepository.findByUsername(trainerName)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerName));
        TrainingType type = getTrainingTypeByName(trainer.getSpecialization().getName());
        Trainee trainee = traineeRepository.findByUsername(traineeName)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + traineeName));
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
        if (!ValidationUtils.isValidTrainingTypeEnum(typeName)) {
            throw new IllegalArgumentException("TrainingType '" + typeName + "' is not supported.");
        }
        return trainingTypeRepository.findByName(typeName)
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + typeName));
    }
}
