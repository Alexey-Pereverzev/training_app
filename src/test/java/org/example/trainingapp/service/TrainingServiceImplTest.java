package org.example.trainingapp.service;

import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;


    @Test
    void whenCreatingTraining_shouldCallDaoSave() {
        // given
        Training training = buildMockTraining(1L);
        // when
        trainingService.createTraining(training);
        // then
        verify(trainingDao).save(training);
    }

    @Test
    void whenGettingTraining_shouldReturnTraining() {
        // given
        Training training = buildMockTraining(2L);
        when(trainingDao.findById(2L)).thenReturn(Optional.of(training));
        // when
        Training result = trainingService.getTraining(2L);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getTrainingName()).isEqualTo("Power Yoga");
        assertThat(result.getTrainingType().getName()).isEqualTo("Yoga");
    }

    @Test
    void whenGettingTrainingNotFound_shouldReturnNull() {
        // given
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());
        // when
        Training result = trainingService.getTraining(99L);
        // then
        assertThat(result).isNull();
    }

    @Test
    void whenGettingAllTrainings_shouldReturnList() {
        // given
        Training t1 = buildMockTraining(1L);
        Training t2 = buildMockTraining(2L);
        when(trainingDao.findAll()).thenReturn(Arrays.asList(t1, t2));
        // when
        List<Training> all = trainingService.getAllTrainings();
        // then
        assertThat(all.size()).isEqualTo(2);
    }

    private Training buildMockTraining(Long id) {
        Trainee trainee = new Trainee(100L, "Anna", "Ivanova", "Anna.Ivanova",
                "pass", true, LocalDate.of(1995, 1, 1), "Almaty",
                null, null);
        TrainingType yogaType = new TrainingType("Yoga");
        Trainer trainer = new Trainer(200L, "Elena", "Sokolova", "Elena.Sokolova",
                "pass", true, yogaType, null, null);
        return new Training(id, "Power Yoga", new TrainingType("Yoga"),
                LocalDate.of(2024, 5, 10), 60, trainee, trainer);
    }
}
