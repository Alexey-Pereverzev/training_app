package org.example.trainingapp.service.impl;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ActionType;
import org.example.trainingapp.dto.TrainingUpdateRequest;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainingSyncServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerHoursPublisher trainerHoursPublisher;

    @Mock
    private Converter converter;

    @InjectMocks
    private TrainingSyncService service;

    private Training training;

    @BeforeEach
    void setUp() {
        MDC.clear();
        training = Training.builder()
                .trainingName("Power Yoga")
                .trainingDate(LocalDate.of(2024, 5, 10))
                .trainingDuration(60)
                .trainer(Trainer.builder().username("Elena.Sokolova").build())
                .trainee(Trainee.builder().username("Nina.Rakhimova").build())
                .trainingType(new TrainingType("Yoga"))
                .build();
    }


    @Test
    void whenSyncTrainerHours_successful_shouldClearAndNotifyAll() {
        // given
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        TrainingUpdateRequest update = TrainingUpdateRequest.builder().build();
        when(converter.trainingAndActionToUpdateRequest(training, ActionType.ADD))
                .thenReturn(update);
        // when
        service.syncTrainerHours();
        // then
        verify(trainerHoursPublisher).publishClearAll(anyString());
        verify(trainingRepository).findAll();
        verify(converter).trainingAndActionToUpdateRequest(training, ActionType.ADD);
        verify(trainerHoursPublisher).publishUpdate(eq(update), anyString());
        assertNull(MDC.get("txId"), "MDC txId should be cleared");
    }


    @Test
    void whenSyncTrainerHours_clearFails_shouldStopInitialization() {
        // given
        doThrow(new RuntimeException("down")).when(trainerHoursPublisher).publishClearAll(anyString());
        // when + then
        assertThrows(RuntimeException.class, () -> service.syncTrainerHours());
        verify(trainingRepository, never()).findAll();
        assertNull(MDC.get("txId"));
    }


    @Test
    void whenSyncTrainerHours_converterFails_shouldSkipThatTraining() {
        // given
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        when(converter.trainingAndActionToUpdateRequest(any(), any()))
                .thenThrow(new RuntimeException("bad data"));
        // when
        service.syncTrainerHours();
        // then
        verify(trainingRepository).findAll();
        verify(trainerHoursPublisher, never()).publishUpdate(any(TrainingUpdateRequest.class), anyString());
        assertNull(MDC.get("txId"));
    }


    @Test
    void whenSyncTrainerHours_notifyFails_shouldSkipThatTraining() {
        // given
        Training training = buildTraining();
        TrainingUpdateRequest update = TrainingUpdateRequest.builder().build();
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        when(converter.trainingAndActionToUpdateRequest(training, ActionType.ADD)).thenReturn(update);
        doThrow(new RuntimeException("down")).when(trainerHoursPublisher).publishUpdate(eq(update), anyString());
        // when
        service.syncTrainerHours();
        // then
        verify(trainerHoursPublisher).publishUpdate(eq(update), anyString());
    }


    private Training buildTraining() {
        Trainer trainer = Trainer.builder().username("Elena.Sokolova").trainees(new ArrayList<>()).build();
        Trainee trainee = Trainee.builder().username("Nina.Rakhimova").trainers(new ArrayList<>()).build();
        return Training.builder()
                .trainingName("Power Yoga")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(new TrainingType("Yoga"))
                .build();
    }
}

