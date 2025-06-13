package org.example.trainingapp.service;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TrainingDao;
import org.example.trainingapp.dto.TrainingDto;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private Converter converter;

    @InjectMocks
    private TrainingServiceImpl trainingService;


    @Test
    void whenCreatingTraining_shouldCallDaoSave() {
        // given
        TrainingDto trainingDto = TrainingDto.builder()
                .trainingName("Power Yoga")
                .trainingType("Yoga")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .traineeId(1L)
                .trainerId(2L)
                .build();
        Training trainingEntity = buildMockTraining(1L);
        when(converter.dtoToEntity(trainingDto)).thenReturn(trainingEntity);
        // when
        trainingService.createTraining("testUsername", "testPassword", trainingDto);
        // then
        verify(trainingDao).save(trainingEntity);
    }

    @Test
    void whenGettingTraining_shouldReturnTrainingDto() {
        // given
        Long id = 1L;
        Training trainingEntity = Training.builder()
                .id(id)
                .trainingName("Yoga Session")
                .build();
        TrainingDto trainingDto = TrainingDto.builder()
                .id(id)
                .trainingName("Yoga Session")
                .build();
        when(trainingDao.findById(id)).thenReturn(Optional.of(trainingEntity));
        when(converter.entityToDto(trainingEntity)).thenReturn(trainingDto);
        // when
        TrainingDto result = trainingService.getTraining("username", "password", id);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getTrainingName()).isEqualTo("Yoga Session");
    }

    @Test
    void whenGettingTrainingNotFound_shouldThrowException() {
        // given
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());
        // when, then
        assertThatThrownBy(() -> trainingService.getTraining("testUsername", "testPassword", 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Training not found with ID: 99");
    }


    @Test
    void whenGettingAllTrainings_shouldReturnListOfDtos() {
        // given
        Training training1 = Training.builder().id(1L).build();
        Training training2 = Training.builder().id(2L).build();
        TrainingDto dto1 = TrainingDto.builder().id(1L).build();
        TrainingDto dto2 = TrainingDto.builder().id(2L).build();

        when(trainingDao.findAll()).thenReturn(List.of(training1, training2));
        when(converter.entityToDto(training1)).thenReturn(dto1);
        when(converter.entityToDto(training2)).thenReturn(dto2);
        // when
        List<TrainingDto> result = trainingService.getAllTrainings("username", "password");
        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TrainingDto::getId).containsExactlyInAnyOrder(1L, 2L);
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
