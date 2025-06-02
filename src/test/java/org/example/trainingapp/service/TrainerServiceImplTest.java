package org.example.trainingapp.service;

import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;


    @Test
    void whenCreatingTrainer_shouldGenerateUsernameAndPassword() {
        // given
        Trainer trainer = new Trainer(1L, new User("Dina", "Aliyeva", null, null,
                false), "Yoga");
        when(trainerDao.findAll()).thenReturn(new ArrayList<>());
        // when
        trainerService.createTrainer(trainer);
        // then
        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());

        Trainer saved = captor.getValue();
        assertThat(saved.getUser().getUsername()).isEqualTo("Dina.Aliyeva");
        assertThat(saved.getUser().getPassword()).isNotNull();
        assertThat(saved.getUser().isActive()).isTrue();
    }


    @Test
    void whenGettingTrainer_shouldReturnTrainer() {
        // given
        Trainer trainer = new Trainer(2L, new User("Bauyrzhan", "Tulegenov",
                "Bauyrzhan.Tulegenov", "pass123", true), "Boxing");
        when(trainerDao.findById(2L)).thenReturn(Optional.of(trainer));
        // when
        Trainer result = trainerService.getTrainer(2L);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getSpecialization()).isEqualTo("Boxing");
    }


    @Test
    void whenUpdatingTrainer_shouldCallDaoUpdate() {
        // given
        Trainer trainer = new Trainer(3L, new User("Aruzhan", "Kairat", "Aruzhan.Kairat",
                "secure", true), "Pilates");
        // when
        trainerService.updateTrainer(trainer);
        // then
        verify(trainerDao).update(trainer);
    }
}
