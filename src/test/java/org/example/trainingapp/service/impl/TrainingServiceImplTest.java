package org.example.trainingapp.service.impl;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.util.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private Converter converter;

    @InjectMocks
    private TrainingServiceImpl trainingService;


    @Test
    void whenCreatingTraining_shouldCallDaoSave() {
        // given
        TrainingRequestDto req = TrainingRequestDto.builder()
                .name("Power Yoga")
                .date(LocalDate.of(2024, 5, 10))
                .duration(60)
                .traineeName("Anna.Ivanova")
                .trainerName("Elena.Sokolova")
                .build();
        Training entity = buildMockTraining();
        when(converter.dtoToEntity(req)).thenReturn(entity);
        when(trainerDao.findByUsernameWithTrainees("Elena.Sokolova")).thenReturn(Optional.of(entity.getTrainer()));
        when(traineeDao.findByUsernameWithTrainers("Anna.Ivanova")).thenReturn(Optional.of(entity.getTrainee()));
        doNothing().when(trainerDao).update(any());
        String authHeader = TestUtils.createAuthHeader("Elena.Sokolova", "pw");
        try (MockedStatic<AuthUtil> ignored = TestUtils.mockDecodeAuth("Elena.Sokolova", "pw")) {
            // when
            String result = trainingService.createTraining(authHeader, req);
            // then
            verify(trainingDao).save(entity);
            assertThat(result).isEqualTo("Power Yoga");
        }
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
