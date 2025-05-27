package org.example.trainingapp.service;

import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TraineeServiceImplTest {

    private TraineeDao traineeDao;
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeDao(traineeDao);
    }


    @Test
    void testCreateTrainee_shouldGenerateUsernameAndPassword() {
        Trainee t = new Trainee(1L, new User("Ivan", "Petrov", null, null,
                false), LocalDate.of(1990, 1, 1), "Almaty");

        when(traineeDao.findAll()).thenReturn(new ArrayList<>());
        traineeService.createTrainee(t);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).save(captor.capture());

        Trainee saved = captor.getValue();
        assertEquals("Ivan.Petrov", saved.getUser().getUsername());
        assertNotNull(saved.getUser().getPassword());
        assertTrue(saved.getUser().isActive());
    }


    @Test
    void testGetTrainee_shouldReturnTrainee() {
        Trainee t = new Trainee(2L, new User("Anna", "Ivanova", "Anna.Ivanova",
                "pass", true), LocalDate.of(1995, 2, 2), "Astana");

        when(traineeDao.findById(2L)).thenReturn(Optional.of(t));

        Trainee result = traineeService.getTrainee(2L);
        assertNotNull(result);
        assertEquals("Anna.Ivanova", result.getUser().getUsername());
    }


    @Test
    void testDeleteTrainee_shouldCallDao() {
        traineeService.deleteTrainee(99L);
        verify(traineeDao, times(1)).deleteById(99L);
    }

    @Test
    void testUpdateTrainee_shouldCallDaoUpdate() {
        Trainee trainee = new Trainee(50L, new User("Nina", "Rakhimova",
                "Nina.Rakhimova", "pw123", true), LocalDate.of(1990, 2, 2),
                "Astana");

        traineeService.updateTrainee(trainee);

        verify(traineeDao).update(trainee);
    }
}