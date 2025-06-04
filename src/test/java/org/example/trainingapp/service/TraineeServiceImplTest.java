package org.example.trainingapp.service;

import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeServiceImpl traineeService;


    @Test
    void whenCreatingTrainee_shouldGenerateUsernameAndPassword() {
        // given
        Trainee t = new Trainee(1L, "Ivan", "Petrov", null, null, false,
                LocalDate.of(1990, 1, 1), "Almaty", null, null);
        when(traineeDao.findAll()).thenReturn(new ArrayList<>());
        // when
        traineeService.createTrainee(t);
        // then
        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).save(captor.capture());

        Trainee saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("Ivan.Petrov");
        assertThat(saved.getPassword()).isNotNull();
        assertThat(saved.isActive()).isTrue();
    }


    @Test
    void whenGettingTrainee_shouldReturnTrainee() {
        // given
        Trainee t = new Trainee(2L, "Anna", "Ivanova", "Anna.Ivanova", "pass",
                true, LocalDate.of(1995, 2, 2), "Astana", null, null);
        when(traineeDao.findById(2L)).thenReturn(Optional.of(t));
        // when
        Trainee result = traineeService.getTrainee(2L);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("Anna.Ivanova");
    }


    @Test
    void whenDeletingTrainee_shouldCallDao() {
        // when
        traineeService.deleteTrainee(99L);
        // then
        verify(traineeDao, times(1)).deleteById(99L);
    }

    @Test
    void whenUpdatingTrainee_shouldCallDaoUpdate() {
        // given
        Trainee trainee = new Trainee(50L, "Nina", "Rakhimova", "Nina.Rakhimova",
                "pw123", true, LocalDate.of(1990, 2, 2), "Astana",
                null, null);
        // when
        traineeService.updateTrainee(trainee);
        // then
        verify(traineeDao).update(trainee);
    }
}