package org.example.trainingapp.service;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private Converter converter;

    @InjectMocks
    private TrainerServiceImpl trainerService;


    @Test
    void whenCreatingTrainer_shouldGenerateUsernameAndPassword() {
        // given
        TrainerDto trainerDto = TrainerDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .build();
        TrainingType yoga = new TrainingType();
        yoga.setId(1L);
        yoga.setName("Yoga");
        Trainer trainerEntity = Trainer.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specialization(yoga)
                .build();
        when(converter.dtoToEntity(trainerDto)).thenReturn(trainerEntity);
        when(trainerDao.findAll()).thenReturn(new ArrayList<>());
        // when
        trainerService.createTrainer(trainerDto);
        // then
        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());
        Trainer saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("Dina.Aliyeva");
        assertThat(saved.getPassword()).isNotNull();
        assertThat(saved.getPassword().length()).isEqualTo(10);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getSpecialization()).isNotNull();
        assertThat(saved.getSpecialization().getName()).isEqualTo("Yoga");
    }


    @Test
    void whenUpdatingTrainer_shouldCallDaoUpdate() {
        // given
        TrainerDto trainerDto = TrainerDto.builder()
                .id(10L)
                .firstName("Nina")
                .lastName("Petrova")
                .specializationName("Boxing")
                .build();
        TrainingType boxing = new TrainingType();
        boxing.setId(2L);
        boxing.setName("Boxing");
        Trainer trainerEntity = Trainer.builder()
                .id(10L)
                .firstName("Nina")
                .lastName("Petrova")
                .specialization(boxing)
                .build();
        when(trainerDao.findById(10L)).thenReturn(Optional.of(trainerEntity));
        when(trainingTypeDao.findByName("Boxing")).thenReturn(Optional.of(boxing));
        // when
        trainerService.updateTrainer("Nina.Petrova", "pw123", trainerDto);
        // then
        verify(trainerDao).update(trainerEntity);
        assertThat(trainerEntity.getSpecialization()).isNotNull();
        assertThat(trainerEntity.getSpecialization().getName()).isEqualTo("Boxing");
    }


    @Test
    void whenGettingTrainer_shouldReturnTrainerDto() {
        // given
        Long trainerId = 2L;
        Trainer trainer = Trainer.builder()
                .id(trainerId)
                .firstName("Bauyrzhan")
                .lastName("Tulegenov")
                .username("Bauyrzhan.Tulegenov")
                .build();
        TrainerDto trainerDto = TrainerDto.builder()
                .id(trainerId)
                .firstName("Bauyrzhan")
                .lastName("Tulegenov")
                .build();
        when(trainerDao.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(converter.entityToDto(trainer)).thenReturn(trainerDto);
        // when
        TrainerDto result = trainerService.getTrainer("Bauyrzhan.Tulegenov", "pw", trainerId);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Bauyrzhan");
    }


    @Test
    void whenGettingTrainerByUsername_shouldReturnTrainerDto() {
        // given
        String username = "Oksana.Mikhaylova";
        Trainer trainer = Trainer.builder()
                .id(5L)
                .firstName("Oksana")
                .lastName("Mikhaylova")
                .username(username)
                .build();
        TrainerDto trainerDto = TrainerDto.builder()
                .id(5L)
                .firstName("Oksana")
                .lastName("Mikhaylova")
                .build();
        when(trainerDao.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(converter.entityToDto(trainer)).thenReturn(trainerDto);
        // when
        TrainerDto result = trainerService.getTrainerByUsername(username, "pw");
        // then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Oksana");
    }


    @Test
    void whenGettingAllTrainers_shouldReturnList() {
        // given
        Trainer t1 = Trainer.builder().id(1L).firstName("Anna").lastName("Ivanova").build();
        Trainer t2 = Trainer.builder().id(2L).firstName("Dina").lastName("Aliyeva").build();
        when(trainerDao.findAll()).thenReturn(List.of(t1, t2));
        when(converter.entityToDto(any(Trainer.class)))
                .thenAnswer(invocation -> {
                    Trainer trainer = invocation.getArgument(0);
                    return TrainerDto.builder()
                            .id(trainer.getId())
                            .firstName(trainer.getFirstName())
                            .lastName(trainer.getLastName())
                            .build();
                });
        // when
        List<TrainerDto> result = trainerService.getAllTrainers("username", "pw");
        // then
        assertThat(result).hasSize(2);
    }


    @Test
    void whenChangingTrainerPassword_shouldUpdatePassword() {
        // given
        Trainer trainer = Trainer.builder()
                .id(10L)
                .firstName("Azamat")
                .lastName("Yeszhanov")
                .username("Azamat.Yeszhanov")
                .password("oldPass")
                .build();
        when(trainerDao.findById(10L)).thenReturn(Optional.of(trainer));
        // when
        trainerService.changeTrainerPassword("Azamat.Yeszhanov", "oldPass", 10L, "newPass");
        // then
        assertThat(trainer.getPassword()).isEqualTo("newPass");
        verify(trainerDao).update(trainer);
    }


    @Test
    void whenSettingTrainerActiveStatus_shouldUpdateActiveField() {
        // given
        Trainer trainer = Trainer.builder()
                .id(20L)
                .firstName("Serik")
                .lastName("Nurpeisov")
                .username("Serik.Nurpeisov")
                .active(true)
                .build();
        when(trainerDao.findById(20L)).thenReturn(Optional.of(trainer));
        // when
        trainerService.setTrainerActiveStatus("Serik.Nurpeisov", "pw", 20L, false);
        // then
        assertThat(trainer.isActive()).isFalse();
        verify(trainerDao).update(trainer);
    }


    @Test
    void whenGettingTrainerTrainings_withDateRange_shouldReturnFilteredList() {
        // given
        Trainer trainer = Trainer.builder()
                .id(30L)
                .username("Trainer.Username")
                .trainings(new ArrayList<>())
                .build();
        Training training1 = new Training();
        training1.setTrainingDate(LocalDate.of(2024, 5, 10));
        Training training2 = new Training();
        training2.setTrainingDate(LocalDate.of(2024, 5, 20));
        trainer.getTrainings().add(training1);
        trainer.getTrainings().add(training2);
        when(trainerDao.findByUsernameWithTrainings("Trainer.Username")).thenReturn(Optional.of(trainer));
        when(converter.entityToDto(training2)).thenReturn(
                TrainingDto.builder()
                        .id(2L)
                        .trainingDate(training2.getTrainingDate())
                        .build()
        );
        LocalDate fromDate = LocalDate.of(2024, 5, 15);
        LocalDate toDate = LocalDate.of(2024, 5, 25);
        // when
        List<TrainingDto> result = trainerService.getTrainerTrainings("Trainer.Username", "pw", fromDate, toDate, null);
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTrainingDate()).isEqualTo(training2.getTrainingDate());
    }



}
