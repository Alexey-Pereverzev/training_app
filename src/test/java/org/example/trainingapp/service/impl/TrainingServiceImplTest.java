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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(trainingExecutionMetrics).record(any(Runnable.class));
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
        assertThat(result).isEqualTo("Power Yoga");
    }


    private Training buildMockTraining() {
        Trainee trainee = Trainee.builder()
                .firstName("Anna")
                .lastName("Ivanova")
                .username("Anna.Ivanova")
                .password("pass")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .active(true)
                .trainers(new ArrayList<>())
                .build();
        TrainingType yogaType = new TrainingType("Yoga");
        Trainer trainer = Trainer.builder()
                .username("Elena.Sokolova")
                .firstName("Elena")
                .lastName("Sokolova")
                .password("pass")
                .specialization(yogaType)
                .trainees(new ArrayList<>())
                .active(true)
                .build();
        return Training.builder()
                .trainingName("Power Yoga")
                .trainingDate(LocalDate.of(2024, 5, 10))
                .trainingDuration(60)
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(new TrainingType("Yoga"))
                .build();
    }
}
