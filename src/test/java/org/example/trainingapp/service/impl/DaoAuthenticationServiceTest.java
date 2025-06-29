package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DaoAuthenticationServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private DaoAuthenticationService authService;

    @Test
    void whenTrainerValid_shouldReturnTrainerRole() {
        // given
        Trainer trainer = new Trainer();
        trainer.setPassword("pw123");
        when(trainerDao.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        // when
        Role role = authService.authorize("trainer1", "pw123");
        // then
        assertThat(role).isEqualTo(Role.TRAINER);
    }

    @Test
    void whenTraineeValid_shouldReturnTraineeRole() {
        // given
        Trainee trainee = new Trainee();
        trainee.setPassword("pw456");
        when(trainerDao.findByUsername("trainee1")).thenReturn(Optional.empty());
        when(traineeDao.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        // when
        Role role = authService.authorize("trainee1", "pw456");
        // then
        assertThat(role).isEqualTo(Role.TRAINEE);
    }

    @Test
    void whenInvalidCredentials_shouldThrowException() {
        // given
        when(trainerDao.findByUsername("invalid")).thenReturn(Optional.empty());
        when(traineeDao.findByUsername("invalid")).thenReturn(Optional.empty());
        // when + then
        assertThatThrownBy(() ->
                authService.authorize("invalid", "wrongpass"))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Invalid credentials");
    }
}

