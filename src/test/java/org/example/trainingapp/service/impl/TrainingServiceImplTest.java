package org.example.trainingapp.service.impl;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.metrics.TrainingExecutionMetrics;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private Converter converter;

    @Mock
    private TrainingExecutionMetrics trainingExecutionMetrics;

    @Mock
    private TrainerHoursPublisher trainerHoursPublisher;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        lenient().doAnswer(inv -> { ((Runnable) inv.getArgument(0)).run(); return null; })
                .when(trainingExecutionMetrics).record(any(Runnable.class));
    }


    @Test
    void whenCreatingTraining_shouldCallDaoSave() {
        // given
        String username = "Elena.Sokolova";
        TrainingRequestDto req = TrainingRequestDto.builder()
                .name("Power Yoga")
                .date(LocalDate.of(2024, 5, 10))
                .duration(60)
                .traineeName("Anna.Ivanova")
                .trainerName(username)
                .build();
        Training entity = buildMockTraining();
        when(converter.dtoToEntity(req)).thenReturn(entity);
        when(trainerRepository.findByUsernameWithTrainees(username)).thenReturn(Optional.of(entity.getTrainer()));
        when(traineeRepository.findByUsernameWithTrainers("Anna.Ivanova")).thenReturn(Optional.of(entity.getTrainee()));
        // when
        String result = trainingService.createTraining(req);
        // then
        verify(trainingRepository).save(entity);
        assertThat(result).isEqualTo("Training 2024-05-10#1 - Power Yoga with id null created successfully");
    }


    @Test
    void whenCreateTraining_trainerNotFound_shouldThrowIllegalArgumentException() {
        // given
        LocalDate d = LocalDate.of(2024, 5, 10);
        TrainingRequestDto req = buildReq(d);
        Training tr = buildMockTraining();
        when(converter.dtoToEntity(req)).thenReturn(tr);
        when(trainerRepository.findByUsernameWithTrainees("Elena.Sokolova")).thenReturn(Optional.empty());
        // when + then
        assertThrows(NoSuchElementException.class, () -> trainingService.createTraining(req));
    }


    @Test
    void whenCreateTraining_traineeNotFound_shouldThrowIllegalArgumentException() {
        // given
        LocalDate d = LocalDate.of(2024, 5, 10);
        TrainingRequestDto req = buildReq(d);
        Training tr = buildMockTraining();
        when(converter.dtoToEntity(req)).thenReturn(tr);
        when(trainerRepository.findByUsernameWithTrainees("Elena.Sokolova"))
                .thenReturn(Optional.of(tr.getTrainer()));
        when(traineeRepository.findByUsernameWithTrainers("Anna.Ivanova"))
                .thenReturn(Optional.empty());
        // when + then
        assertThrows(NoSuchElementException.class, () -> trainingService.createTraining(req));
    }


    @Test
    void whenCreateTraining_existingTrainings_thenNameShouldIncrementIndex() {
        // given
        String username = "Elena.Sokolova";
        TrainingRequestDto req = TrainingRequestDto.builder()
                .name("Power Yoga")
                .date(LocalDate.of(2024, 5, 10))
                .duration(60)
                .traineeName("Anna.Ivanova")
                .trainerName(username)
                .build();
        Training entity = buildMockTraining();
        when(converter.dtoToEntity(req)).thenReturn(entity);
        when(trainerRepository.findByUsernameWithTrainees(username)).thenReturn(Optional.of(entity.getTrainer()));
        when(traineeRepository.findByUsernameWithTrainers("Anna.Ivanova")).thenReturn(Optional.of(entity.getTrainee()));
        Training existing = buildMockTraining();
        existing.setTrainingName("2024-05-10#1 - Old");
        when(trainingRepository.findByTrainer_UsernameAndTrainingDate(username, req.getDate()))
                .thenReturn(List.of(existing));
        // when
        trainingService.createTraining(req);
        // then
        assertThat(entity.getTrainingName()).startsWith("2024-05-10#2 -");
    }


    @Test
    void whenDeleteTrainingByName_shouldRemoveAndNotify() {
        // given
        String name = "2024-05-10#1 - Yoga";
        Training training = buildMockTraining();
        training.setTrainingName(name);
        training.setTrainingDate(LocalDate.now().plusDays(1));
        when(trainingRepository.findByTrainingName(name)).thenReturn(Optional.of(training));
        // when
        trainingService.deleteTrainingByName(name);
        // then
        verify(trainingRepository).delete(training);
        verify(trainerHoursPublisher).publishUpdate(any());
    }


    @Test
    void whenDeleteTrainingByName_pastTraining_shouldThrowIllegalState() {
        // given
        String name = "old";
        Training training = buildMockTraining();
        training.setTrainingName(name);
        training.setTrainingDate(LocalDate.now().minusDays(1));
        when(trainingRepository.findByTrainingName(name)).thenReturn(Optional.of(training));
        // when + then
        assertThrows(IllegalStateException.class, () -> trainingService.deleteTrainingByName(name));
    }


    @Test
    void whenDeleteTrainingByName_trainingNotFound_shouldThrowNoSuchElement() {
        // given
        when(trainingRepository.findByTrainingName("missing")).thenReturn(Optional.empty());
        // when + then
        assertThrows(NoSuchElementException.class, () -> trainingService.deleteTrainingByName("missing"));
    }


    private Training buildMockTraining() {
        LocalDate date = LocalDate.of(2024, 5, 10);
        TrainingType yoga = new TrainingType("Yoga");
        Trainer trainer = Trainer.builder()
                .username("Elena.Sokolova").firstName("Elena").lastName("Sokolova").specialization(yoga)
                .trainees(new ArrayList<>()).active(true).build();
        Trainee trainee = Trainee.builder()
                .username("Anna.Ivanova").firstName("Anna").lastName("Ivanova")
                .trainers(new ArrayList<>()).active(true).build();
        return Training.builder()
                .trainingName("Power Yoga")
                .trainingDate(date)
                .trainingDuration(60)
                .trainingType(yoga)
                .trainer(trainer)
                .trainee(trainee)
                .build();
    }


    private TrainingRequestDto buildReq(LocalDate date) {
        return TrainingRequestDto.builder()
                .name("Yoga")
                .trainerName("Elena.Sokolova")
                .traineeName("Anna.Ivanova")
                .date(date)
                .duration(60)
                .build();
    }

}
