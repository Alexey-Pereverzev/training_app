package org.example.trainingapp.service;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TraineeDao;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private Converter converter;

    @InjectMocks
    private TraineeServiceImpl traineeService;


//    @Test
//    void whenCreatingTrainee_shouldGenerateUsernameAndPassword() {
//        // given
//        TraineeDto traineeDto = TraineeDto.builder()
//                .firstName("Ivan")
//                .lastName("Petrov")
//                .dateOfBirth(LocalDate.of(1990, 1, 1))
//                .address("Almaty")
//                .build();
//        Trainee traineeEntity = Trainee.builder()
//                .firstName("Ivan")
//                .lastName("Petrov")
//                .dateOfBirth(LocalDate.of(1990, 1, 1))
//                .address("Almaty")
//                .build();
//        when(converter.dtoToEntity(traineeDto)).thenReturn(traineeEntity);
//        when(traineeDao.findAll()).thenReturn(new ArrayList<>());
//        // when
//        traineeService.createTrainee(traineeDto);
//        // then
//        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
//        verify(traineeDao).save(captor.capture());
//        Trainee saved = captor.getValue();
//        assertThat(saved.getUsername()).isEqualTo("Ivan.Petrov");
//        assertThat(saved.getPassword()).isNotNull();
//        assertThat(saved.getPassword().length()).isEqualTo(10);
//        assertThat(saved.isActive()).isTrue();
//    }


//    @Test
//    void whenGettingTrainee_shouldReturnTrainee() {
//        // given
//        Trainee t = new Trainee(2L, "Anna", "Ivanova", "Anna.Ivanova", "pass",
//                true, LocalDate.of(1995, 2, 2), "Astana", null, null);
//        when(traineeDao.findById(2L)).thenReturn(Optional.of(t));
//        when(converter.entityToDto(t)).thenReturn(
//                TraineeDto.builder()
//                        .firstName("Anna")
//                        .lastName("Ivanova")
//                        .build()
//        );
//        // when
//        TraineeDto result = traineeService.getTrainee("Anna.Ivanova", "pass",2L);
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getFirstName()).isEqualTo("Anna");
//        assertThat(result.getLastName()).isEqualTo("Ivanova");
//    }


//    @Test
//    void whenDeletingTrainee_shouldCallDao() {
//        // given
//        Trainee mockTrainee = new Trainee();
//        mockTrainee.setId(99L);
//        when(traineeDao.findById(99L)).thenReturn(Optional.of(mockTrainee));
//        // when
//        traineeService.deleteTrainee("testUsername", "testPassword", 99L);
//        // then
//        verify(traineeDao, times(1)).deleteById(99L);
//    }

//    @Test
//    void whenUpdatingTrainee_shouldCallDaoUpdate() {
//        // given
//        TraineeRequestDto traineeRequestDto = TraineeRequestDto.builder()
//                .id(50L)
//                .firstName("Nina")
//                .lastName("Rakhimova")
//                .dateOfBirth(LocalDate.of(1990, 2, 2))
//                .address("Astana")
//                .build();
//        Trainee traineeEntity = Trainee.builder()
//                .id(50L)
//                .firstName("Nina")
//                .lastName("Rakhimova")
//                .dateOfBirth(LocalDate.of(1990, 1, 1))
//                .address("Astana")
//                .build();
//        when(traineeDao.findById(50L)).thenReturn(Optional.of(traineeEntity));
//        // when
//        traineeService.updateTrainee("Nina.Rakhimova", "pw123", traineeRequestDto);
//        // then
//        verify(traineeDao).update(any(Trainee.class));
//    }

    @Test
    void whenChangingTraineePassword_shouldUpdatePassword() {
        // given
        Trainee trainee = new Trainee(10L, "Dina", "Aliyeva", "Dina.Aliyeva",
                "oldPass", true, LocalDate.of(1992, 3, 3), "Almaty",
                null, null);
        when(traineeDao.findById(10L)).thenReturn(Optional.of(trainee));
        // when
        traineeService.changeTraineePassword("Dina.Aliyeva", "oldPass", 10L, "newPass");
        // then
        assertThat(trainee.getPassword()).isEqualTo("newPass");
        verify(traineeDao).update(trainee);
    }

    @Test
    void whenSettingTraineeActiveStatus_shouldUpdateActiveField() {
        // given
        Trainee trainee = new Trainee(20L, "Azamat", "Yeszhanov", "Azamat.Yeszhanov",
                "pw", true, LocalDate.of(1994, 4, 4), "Astana",
                null, null);
        when(traineeDao.findById(20L)).thenReturn(Optional.of(trainee));
        // when
        traineeService.setTraineeActiveStatus("Azamat.Yeszhanov", "pw", 20L, false);
        // then
        assertThat(trainee.isActive()).isFalse();
        verify(traineeDao).update(trainee);
    }

    @Test
    void whenDeletingTraineeByUsername_shouldCallDao() {
        // given
        Trainee trainee = new Trainee(30L, "Sergey", "Shapovalov", "Sergey.Shapovalov",
                "pw", true, LocalDate.of(1993, 5, 5), "Karaganda",
                null, null);
        when(traineeDao.findByUsername("Sergey.Shapovalov")).thenReturn(Optional.of(trainee));
        // when
        traineeService.deleteTraineeByUsername("Sergey.Shapovalov", "pw");
        // then
        verify(traineeDao).deleteById(trainee.getId());
    }

    @Test
    void whenGettingTraineeTrainings_givenTraineeHasNoTrainings_shouldReturnEmptyList() {
        // given
        Trainee trainee = new Trainee(40L, "Elena", "Zharkynbaeva", "Elena.Zharkynbaeva",
                "pw", true, LocalDate.of(1995, 6, 6), "Pavlodar",
                Collections.emptyList(), new ArrayList<>());
        // when
        List<TrainingDto> result = traineeService.getTraineeTrainings("Elena.Zharkynbaeva", "pw",
                null, null, null, null);
        // then
        assertThat(result).isEmpty();
    }


    @Test
    void whenGettingTraineeTrainings_withDateRange_shouldFilterCorrectly() {
        // given
        Trainee trainee = new Trainee(1L, "Dina", "Aliyeva", "Dina.Aliyeva", "pw",
                true, LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(),
                new ArrayList<>());
        Training training1 = new Training();
        training1.setTrainingDate(LocalDate.of(2024, 5, 10));
        Training training2 = new Training();
        training2.setTrainingDate(LocalDate.of(2024, 5, 20));
        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);
        when(converter.entityToDto(training2)).thenReturn(
                TrainingDto.builder()
                        .id(2L)
                        .trainingDate(training2.getTrainingDate())
                        .build());
        when(traineeDao.findByUsernameWithTrainings("Dina.Aliyeva")).thenReturn(Optional.of(trainee));
        LocalDate fromDate = LocalDate.of(2024, 5, 15);
        LocalDate toDate = LocalDate.of(2024, 5, 25);
        // when
        List<TrainingDto> result = traineeService.getTraineeTrainings("Dina.Aliyeva", "pw", fromDate,
                toDate, null, null);
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTrainingDate()).isEqualTo(training2.getTrainingDate());
    }


    @Test
    void whenGettingTraineeTrainings_withTrainerName_shouldFilterCorrectly() {
        // given
        Trainee trainee = new Trainee(2L, "Dina", "Aliyeva", "Dina.Aliyeva", "pw",
                true, LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(),
                new ArrayList<>());

        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setUsername("Arman.Nurpeisov");
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setUsername("Oksana.Mikhaylova");

        Training training1 = new Training();
        training1.setId(101L);
        training1.setTrainer(trainer1);
        Training training2 = new Training();
        training2.setId(102L);

        training2.setTrainer(trainer2);
        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);

        when(converter.entityToDto(training2)).thenReturn(
                TrainingDto.builder()
                        .id(training2.getId())
                        .trainerId(training2.getTrainer().getId())
                        .trainingName(training2.getTrainingName())
                        .build()
        );
        when(traineeDao.findByUsernameWithTrainings("Dina.Aliyeva")).thenReturn(Optional.of(trainee));
        // when
        List<TrainingDto> result = traineeService.getTraineeTrainings("Dina.Aliyeva", "pw", null,
                null, "Oksana.Mikhaylova", null);
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTrainerId()).isEqualTo(2L);
    }


    @Test
    void whenGettingTraineeTrainings_withTrainingType_shouldFilterCorrectly() {
        // given
        Trainee trainee = new Trainee(3L, "Dina", "Aliyeva", "Dina.Aliyeva", "pw",
                true, LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(),
                new ArrayList<>());

        TrainingType yoga = new TrainingType();
        yoga.setId(1L);
        yoga.setName("Yoga");
        TrainingType boxing = new TrainingType();
        boxing.setId(2L);
        boxing.setName("Boxing");

        Training training1 = new Training();
        training1.setTrainingType(yoga);
        Training training2 = new Training();
        training2.setTrainingType(boxing);

        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);

        when(converter.entityToDto(training2)).thenReturn(
                TrainingDto.builder()
                        .id(training2.getId())
                        .trainingType(training2.getTrainingType().getName())
                        .build()
        );
        when(traineeDao.findByUsernameWithTrainings("Dina.Aliyeva")).thenReturn(Optional.of(trainee));
        // when
        List<TrainingDto> result = traineeService.getTraineeTrainings("Dina.Aliyeva", "pw", null,
                null, null, "Boxing");
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTrainingType()).isEqualTo("Boxing");
    }


    @Test
    void whenGettingTraineeTrainings_withNoFilters_shouldReturnAll() {
        // given
        Trainee trainee = new Trainee(4L, "Dina", "Aliyeva", "Dina.Aliyeva", "pw",
                true, LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(),
                new ArrayList<>());

        Training training1 = new Training();
        training1.setId(301L);
        Training training2 = new Training();
        training2.setId(302L);
        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);

        when(converter.entityToDto(any(Training.class)))
                .thenAnswer(invocation -> {
                    Training training = invocation.getArgument(0);
                    return TrainingDto.builder()
                            .id(training.getId())
                            .build();
                });
        when(traineeDao.findByUsernameWithTrainings("Dina.Aliyeva")).thenReturn(Optional.of(trainee));
        // when
        List<TrainingDto> result = traineeService.getTraineeTrainings("Dina.Aliyeva", "pw", null,
                null, null, null);
        // then
        assertThat(result).hasSize(2);
        List<Long> ids = result.stream().map(TrainingDto::getId).toList();
        assertThat(ids).containsExactlyInAnyOrder(301L, 302L);
    }


    @Test
    void whenUpdatingTraineeTrainers_shouldSetNewTrainers() {
        // given
        Trainee trainee = new Trainee(50L, "Aigerim", "Seilkhanova", "Aigerim.Seilkhanova",
                "pw", true, LocalDate.of(1996, 7, 7), "Shymkent",
                null, null);
        when(traineeDao.findByUsername("Aigerim.Seilkhanova")).thenReturn(Optional.of(trainee));
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer1));
        // when
        traineeService.updateTraineeTrainers("Aigerim.Seilkhanova", "pw", List.of(1L));
        // then
        assertThat(trainee.getTrainers()).contains(trainer1);
        verify(traineeDao).update(trainee);
    }

    @Test
    void whenGettingAvailableTrainersForTrainee_shouldReturnUnassignedTrainers() {
        // given
        Trainee trainee = new Trainee();
        trainee.setId(60L);
        trainee.setUsername("Bagdat.Serikbay");

        Trainer assignedTrainer = new Trainer();
        assignedTrainer.setId(1L);
        assignedTrainer.setFirstName("Anna");
        assignedTrainer.setLastName("Borisova");

        Trainer unassignedTrainer = new Trainer();
        unassignedTrainer.setId(2L);
        unassignedTrainer.setFirstName("Daniyar");
        unassignedTrainer.setLastName("Zhumagulov");

        trainee.setTrainers(List.of(assignedTrainer));
        when(traineeDao.findByUsername("Bagdat.Serikbay")).thenReturn(Optional.of(trainee));
        when(trainerDao.findAll()).thenReturn(List.of(assignedTrainer, unassignedTrainer));
        when(converter.entityToDto(unassignedTrainer)).thenReturn(
                TrainerDto.builder()
                        .id(unassignedTrainer.getId())
                        .firstName(unassignedTrainer.getFirstName())
                        .lastName(unassignedTrainer.getLastName())
                        .build()
        );
        // when
        List<TrainerDto> result = traineeService.getAvailableTrainersForTrainee("Bagdat.Serikbay", "pw");
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFirstName()).isEqualTo("Daniyar");
        assertThat(result.getFirst().getLastName()).isEqualTo("Zhumagulov");
    }


//    @Test
//    void whenGettingTraineeByUsername_shouldReturnTraineeDto() {
//        // given
//        String username = "Dina.Aliyeva";
//        String password = "password123";
//        Trainee trainee = new Trainee(3L, "Dina", "Aliyeva", username, password,
//                true, LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(),
//                new ArrayList<>());
//        TraineeDto traineeDto = TraineeDto.builder()
//                .id(3L)
//                .firstName("Dina")
//                .lastName("Aliyeva")
//                .active(true)
//                .dateOfBirth(LocalDate.of(1992, 3, 3))
//                .address("Almaty")
//                .build();
//        when(traineeDao.findByUsername(username)).thenReturn(Optional.of(trainee));
//        when(converter.entityToDto(trainee)).thenReturn(traineeDto);
//        // when
//        TraineeDto result = traineeService.getTraineeByUsername(username, password);
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getFirstName()).isEqualTo("Dina");
//        assertThat(result.getLastName()).isEqualTo("Aliyeva");
//    }

    @Test
    void whenGettingTraineeByUsername_notFound_shouldThrowException() {
        // given
        String username = "nonexistentUser";
        String password = "password123";
        when(traineeDao.findByUsername(username)).thenReturn(Optional.empty());
        // when + then
        assertThatThrownBy(() -> traineeService.getTraineeByUsername(username, password))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee not found: " + username);
    }
}