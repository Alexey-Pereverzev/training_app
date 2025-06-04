package org.example.trainingapp.service;

import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.TrainingType;
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
        TrainingType yogaType = new TrainingType("Yoga");
        Trainer trainer = new Trainer(1L, "Dina", "Aliyeva", null, null,
                false, yogaType, null, null);
        when(trainerDao.findAll()).thenReturn(new ArrayList<>());
        // when
        trainerService.createTrainer(trainer);
        // then
        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());

        Trainer saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("Dina.Aliyeva");
        assertThat(saved.getPassword()).isNotNull();
        assertThat(saved.isActive()).isTrue();
    }


    @Test
    void whenGettingTrainer_shouldReturnTrainer() {
        // given
        TrainingType boxingType = new TrainingType("Boxing");
        Trainer trainer = new Trainer(2L, "Bauyrzhan", "Tulegenov", "Bauyrzhan.Tulegenov",
                "pass123", true, boxingType, null, null);
        when(trainerDao.findById(2L)).thenReturn(Optional.of(trainer));
        // when
        Trainer result = trainerService.getTrainer(2L);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getSpecialization().getName()).isEqualTo("Boxing");
    }


    @Test
    void whenUpdatingTrainer_shouldCallDaoUpdate() {
        // given
        TrainingType pilatesType = new TrainingType("Pilates");
        Trainer trainer = new Trainer(3L, "Aruzhan", "Kairat", "Aruzhan.Kairat",
                "secure", true, pilatesType, null, null);
        // when
        trainerService.updateTrainer(trainer);
        // then
        verify(trainerDao).update(trainer);
    }
}
