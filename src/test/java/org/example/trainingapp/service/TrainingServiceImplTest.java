package org.example.trainingapp.service;

import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.entity.*;
import org.example.trainingapp.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TrainingServiceImplTest {

    private TrainingDao trainingDao;
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        trainingService = new TrainingServiceImpl();
        trainingService.setTrainingDao(trainingDao);
    }


    @Test
    void testCreateTraining_shouldCallDaoSave() {
        Training training = buildMockTraining(1L);
        trainingService.createTraining(training);
        verify(trainingDao, times(1)).save(training);
    }


    @Test
    void testGetTraining_shouldReturnTraining() {
        Training training = buildMockTraining(2L);

        when(trainingDao.findById(2L)).thenReturn(Optional.of(training));
        Training result = trainingService.getTraining(2L);

        assertNotNull(result);
        assertEquals("Power Yoga", result.getTrainingName());
        assertEquals("Yoga", result.getTrainingType().getName());
    }


    @Test
    void testGetTraining_whenNotFound_shouldReturnNull() {
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());
        Training result = trainingService.getTraining(99L);
        assertNull(result);
    }


    @Test
    void testGetAllTrainings_shouldReturnList() {
        Training t1 = buildMockTraining(1L);
        Training t2 = buildMockTraining(2L);
        when(trainingDao.findAll()).thenReturn(Arrays.asList(t1, t2));
        assertEquals(2, trainingService.getAllTrainings().size());
    }


    private Training buildMockTraining(Long id) {
        Trainee trainee = new Trainee(100L, new User("Anna", "Ivanova", "Anna.Ivanova",
                "pass", true), LocalDate.of(1995, 1, 1), "Almaty");
        Trainer trainer = new Trainer(200L, new User("Elena", "Sokolova", "Elena.Sokolova",
                "pass", true), "Yoga");
        return new Training(id, "Power Yoga", new TrainingType("Yoga"), LocalDate.of(2024, 5,
                10), 60, trainee, trainer);
    }
}
