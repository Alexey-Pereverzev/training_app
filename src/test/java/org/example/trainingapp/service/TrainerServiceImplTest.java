package org.example.trainingapp.service;

import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TrainerServiceImplTest {

    private TrainerDao trainerDao;
    private TrainerServiceImpl trainerService;


    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        trainerService = new TrainerServiceImpl();
        trainerService.setTrainerDao(trainerDao);
    }


    @Test
    void testCreateTrainer_shouldGenerateUsernameAndPassword() {
        Trainer trainer = new Trainer(1L, new User("Dina", "Aliyeva", null, null,
                false), "Yoga");

        when(trainerDao.findAll()).thenReturn(new ArrayList<>());

        trainerService.createTrainer(trainer);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());

        Trainer saved = captor.getValue();
        assertEquals("Dina.Aliyeva", saved.getUser().getUsername());
        assertNotNull(saved.getUser().getPassword());
        assertTrue(saved.getUser().isActive());
    }


    @Test
    void testGetTrainer_shouldReturnTrainer() {
        Trainer trainer = new Trainer(2L, new User("Bauyrzhan", "Tulegenov",
                "Bauyrzhan.Tulegenov", "pass123", true), "Boxing");

        when(trainerDao.findById(2L)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainer(2L);
        assertNotNull(result);
        assertEquals("Boxing", result.getSpecialization());
    }


    @Test
    void testUpdateTrainer_shouldCallDaoUpdate() {
        Trainer trainer = new Trainer(3L, new User("Aruzhan", "Kairat", "Aruzhan.Kairat",
                "secure", true), "Pilates");

        trainerService.updateTrainer(trainer);
        verify(trainerDao).update(trainer);
    }
}
