package org.example.trainingapp.service.impl;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.exception.ForbiddenAccessException;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.util.AuthContextUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private Converter converter;

    @Mock
    private AuthContextUtil authContextUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl traineeService;


    @Test
    void whenUpdatingTrainee_shouldCallDaoUpdate() {
        // given
        String username = "Nina.Rakhimova";
        TraineeRequestDto traineeRequestDto = TraineeRequestDto.builder()
                .firstName("Nina")
                .lastName("Rakhimova")
                .dateOfBirth(LocalDate.of(1990, 2, 2))
                .address("Astana")
                .active(true)
                .build();
        Trainee traineeEntity = Trainee.builder()
                .id(50L)
                .firstName("Nina")
                .lastName("Rakhimova")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Astana")
                .trainers(new ArrayList<>())
                .build();
        when(traineeRepository.findByUsernameWithTrainers(username)).thenReturn(Optional.of(traineeEntity));
        // when
        traineeRequestDto.setUsername(username);
        traineeService.updateTrainee(traineeRequestDto);
        // then
        verify(traineeRepository).save(any(Trainee.class));
    }


    @Test
    void whenUpdatingTraineeTrainers_andTrainerNotFound_shouldThrowForbiddenAccessException() {
        // given
        String currentUser = "Elena.Zharkynbaeva";
        String otherUser = "Dina.Aliyeva";
        when(authContextUtil.getUsername()).thenReturn(currentUser);
        // when + then
        assertThatThrownBy(() -> traineeService.deleteTrainee(otherUser))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("User is not the owner of entity");
        verify(traineeRepository, never()).delete(any());
    }


    @Test
    void whenDeletingTrainee_ownAccount_shouldCallDao() {
        // given
        String username = "Elena.Zharkynbaeva";
        when(authContextUtil.getUsername()).thenReturn(username);
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setTrainings(new ArrayList<>());
        trainee.setTrainers(new ArrayList<>());
        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.of(trainee));
        // when
        traineeService.deleteTrainee(username);
        // then
        verify(traineeRepository).delete(trainee);
    }


    @Test
    void whenDeletingTrainee_withBlankUsername_shouldThrowException() {
        // given
        String blankUsername = "   ";
        when(authContextUtil.getUsername()).thenReturn(blankUsername);
        // when + then
        assertThatThrownBy(() -> traineeService.deleteTrainee(blankUsername))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Trainee not found:    ");
    }


    @Test
    void whenDeletingTrainee_anotherUsersAccount_shouldThrowForbiddenAccessException() {
        // given
        String currentUser = "Elena.Zharkynbaeva";
        String otherUser = "Dina.Aliyeva";
        when(authContextUtil.getUsername()).thenReturn(currentUser);
        // when + then
        assertThatThrownBy(() -> traineeService.deleteTrainee(otherUser))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("User is not the owner of entity");
        verify(traineeRepository, never()).delete(any());
    }


    @Test
    void whenDeletingTrainee_notFound_shouldThrowNoSuchElementException() {
        // given
        String username = "Missing.User";
        when(authContextUtil.getUsername()).thenReturn(username);
        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.empty());
        // when + then
        assertThatThrownBy(() -> traineeService.deleteTrainee(username))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Trainee not found");
    }


    @Test
    void whenDeletingTraineeWithValidAuth_shouldDeleteEntity() {
        // given
        String username = "Sergey.Shapovalov";
        when(authContextUtil.getUsername()).thenReturn(username);
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setTrainings(new ArrayList<>());
        trainee.setTrainers(new ArrayList<>());
        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.of(trainee));
        // when
        traineeService.deleteTrainee(username);
        // then
        verify(traineeRepository).delete(trainee);
    }


    @Test
    void whenGettingTraineeByUsername_shouldReturnTraineeDto() {
        // given
        String username = "Dina.Aliyeva";
        String password = "password123";
        Trainee trainee = new Trainee(3L, "Dina", "Aliyeva", username, password, true,
                LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(), new ArrayList<>());
        List<TrainerShortDto> trainerShortDtos = new ArrayList<>();
        TraineeResponseDto expectedDto = TraineeResponseDto.builder()
                .username(username)
                .firstName("Dina")
                .lastName("Aliyeva")
                .dateOfBirth(LocalDate.of(1992, 3, 3))
                .address("Almaty")
                .active(true)
                .trainers(trainerShortDtos)
                .build();
        when(authContextUtil.getUsername()).thenReturn(username);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.findByUsernameWithTrainers(username)).thenReturn(Optional.of(trainee));
        when(converter.entityToDtoWithoutUsername(trainee, trainerShortDtos)).thenReturn(expectedDto);
        // when
        TraineeResponseDto result = traineeService.getTraineeByUsername(username);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getFirstName()).isEqualTo("Dina");
        assertThat(result.getLastName()).isEqualTo("Aliyeva");
        assertThat(result.getAddress()).isEqualTo("Almaty");
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1992, 3, 3));
        assertThat(result.getActive()).isTrue();
        assertThat(result.getTrainers()).isEqualTo(trainerShortDtos);
    }


    @Test
    void whenGettingTraineeByUsername_notFound_shouldThrowException() {
        // given
        String username = "nonexistentUser";
        when(authContextUtil.getUsername()).thenReturn(username);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());
        // when + then
        assertThatThrownBy(() -> traineeService.getTraineeByUsername(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Trainee not found: " + username);
    }


    @Test
    void whenSettingTraineeActiveStatus_shouldUpdateActiveField() {
        // given
        String username = "Azamat.Yeszhanov";
        Trainee trainee = new Trainee(20L, "Azamat", "Yeszhanov", username, "pw",
                true, LocalDate.of(1994, 4, 4), "Astana", null, null);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        // when
        ActiveStatusDto dto = new ActiveStatusDto(username, false);
        traineeService.setTraineeActiveStatus(dto);
        // then
        assertThat(trainee.isActive()).isFalse();
        verify(traineeRepository).save(trainee);
    }


    @Test
    void whenGettingTraineeTrainings_andTraineeHasNoTrainings_shouldReturnEmptyList() {
        // given
        String username = "Elena.Zharkynbaeva";
        Trainee trainee = new Trainee(40L, "Elena", "Zharkynbaeva", username, "pw",
                true, LocalDate.of(1995, 6, 6), "Pavlodar", Collections.emptyList(),
                new ArrayList<>());
        when(authContextUtil.getUsername()).thenReturn(username);
        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.of(trainee));

        // when
        List<TrainingResponseDto> result = traineeService.getTraineeTrainings(username, null, null,
                null, null);

        // then
        assertThat(result).isEmpty();
    }


    @Test
    void whenGettingTraineeTrainings_withDateRange_shouldFilterCorrectly() {
        // given
        String username = "Dina.Aliyeva";
        Trainee trainee = new Trainee(1L, "Dina", "Aliyeva", username, "pw", true,
                LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(), new ArrayList<>());
        Training training1 = new Training();
        training1.setTrainingDate(LocalDate.of(2024, 5, 10));
        training1.setTrainingName("Kickboxing");
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("Arman.Nurpeisov");
        training1.setTrainer(trainer1);

        Training training2 = new Training();
        training2.setTrainingDate(LocalDate.of(2024, 5, 20));
        training2.setTrainingName("Boxing Basics");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("Oksana.Mikhaylova");
        training2.setTrainer(trainer2);

        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);

        when(converter.entityToDtoWithNullTrainee(training2)).thenReturn(
                TrainingResponseDto.builder()
                        .name(training2.getTrainingName())
                        .date(training2.getTrainingDate())
                        .trainerName(training2.getTrainer().getUsername())
                        .build()
        );
        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.of(trainee));
        LocalDate fromDate = LocalDate.of(2024, 5, 15);
        LocalDate toDate = LocalDate.of(2024, 5, 25);
        when(authContextUtil.getUsername()).thenReturn(username);
        // when
        List<TrainingResponseDto> result = traineeService.getTraineeTrainings(username, fromDate, toDate, null,
                null);
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDate()).isEqualTo(training2.getTrainingDate());
        assertThat(result.getFirst().getName()).isEqualTo("Boxing Basics");
    }


    @Test
    void whenGettingTraineeTrainings_withTrainerName_shouldFilterCorrectly() {
        // given
        String username = "Dina.Aliyeva";
        Trainee trainee = new Trainee(2L, "Dina", "Aliyeva", username, "pw", true,
                LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(), new ArrayList<>());

        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setUsername("Arman.Nurpeisov");
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setUsername("Oksana.Mikhaylova");

        Training training1 = new Training();
        training1.setId(101L);
        training1.setTrainer(trainer1);
        training1.setTrainingName("Kickboxing");

        Training training2 = new Training();
        training2.setId(102L);
        training2.setTrainer(trainer2);
        training2.setTrainingName("Boxing Basics");

        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);

        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.of(trainee));
        when(converter.entityToDtoWithNullTrainee(training2)).thenReturn(
                TrainingResponseDto.builder()
                        .name("Boxing Basics")
                        .trainerName("Oksana.Mikhaylova")
                        .build()
        );
        when(authContextUtil.getUsername()).thenReturn(username);
        // when
        List<TrainingResponseDto> result = traineeService.getTraineeTrainings(username, null, null,
                "Oksana.Mikhaylova", null);
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Boxing Basics");
        assertThat(result.getFirst().getTrainerName()).isEqualTo("Oksana.Mikhaylova");
    }


    @Test
    void whenGettingTraineeTrainings_withTrainingType_shouldFilterCorrectly() {
        // given
        String username = "Dina.Aliyeva";
        Trainee trainee = Trainee.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .username(username)
                .password("pw")
                .active(true)
                .dateOfBirth(LocalDate.of(1992, 3, 3))
                .address("Almaty")
                .trainers(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();
        TrainingType yoga = new TrainingType();
        yoga.setId(1L);
        yoga.setName("Yoga");
        TrainingType boxing = new TrainingType();
        boxing.setId(2L);
        boxing.setName("Boxing");

        Training training1 = new Training();
        training1.setTrainingType(yoga);
        Trainer arman = new Trainer();
        arman.setUsername("Arman.Nurpeisov");
        training1.setTrainer(arman);

        Training training2 = new Training();
        training2.setTrainingType(boxing);
        Trainer oksana = new Trainer();
        oksana.setUsername("Oksana.Mikhaylova");
        training2.setTrainer(oksana);

        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);

        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.of(trainee));
        when(converter.entityToDtoWithNullTrainee(training2)).thenReturn(
                TrainingResponseDto.builder()
                        .type("Boxing")
                        .build()
        );
        when(authContextUtil.getUsername()).thenReturn(username);
        // when
        List<TrainingResponseDto> result = traineeService.getTraineeTrainings(username, null, null,
                "Oksana.Mikhaylova", null);
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getType()).isEqualTo("Boxing");
    }


    @Test
    void whenGettingTraineeTrainings_withNoFilters_shouldReturnAll() {
        // given
        String username = "Dina.Aliyeva";
        Trainee trainee = new Trainee(4L, "Dina", "Aliyeva", username, "pw", true,
                LocalDate.of(1992, 3, 3), "Almaty", new ArrayList<>(), new ArrayList<>());

        Training training1 = new Training();
        training1.setTrainingName("Session 1");
        training1.setTrainingDate(LocalDate.of(2024, 6, 1));
        Trainer arman = new Trainer();
        arman.setUsername("Arman.Nurpeisov");
        training1.setTrainer(arman);

        Training training2 = new Training();
        training2.setTrainingName("Session 2");
        training2.setTrainingDate(LocalDate.of(2024, 6, 2));
        Trainer oksana = new Trainer();
        oksana.setUsername("Oksana.Mikhaylova");
        training2.setTrainer(oksana);

        trainee.getTrainings().add(training1);
        trainee.getTrainings().add(training2);

        when(converter.entityToDtoWithNullTrainee(any(Training.class)))
                .thenAnswer(invocation -> {
                    Training training = invocation.getArgument(0);
                    return TrainingResponseDto.builder()
                            .name(training.getTrainingName())
                            .date(training.getTrainingDate())
                            .build();
                });
        when(traineeRepository.findByUsernameWithTrainings(username)).thenReturn(Optional.of(trainee));
        when(authContextUtil.getUsername()).thenReturn(username);
        // when
        List<TrainingResponseDto> result = traineeService.getTraineeTrainings(username, null, null,
                null, null);
        // then
        assertThat(result).hasSize(2);
        List<String> names = result.stream().map(TrainingResponseDto::getName).toList();
        assertThat(names).containsExactlyInAnyOrder("Session 1", "Session 2");
    }


    @Test
    void whenUpdatingTraineeTrainers_shouldSetNewTrainers() {
        // given
        String username = "Aigerim.Seilkhanova";
        Trainee trainee = new Trainee(50L, "Aigerim", "Seilkhanova", username, "pw",
                true, LocalDate.of(1996, 7, 7), "Shymkent", new ArrayList<>(),
                new ArrayList<>());
        when(traineeRepository.findByUsernameWithTrainers(username)).thenReturn(Optional.of(trainee));
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setUsername("Arman.Nurpeisov");
        trainer1.setTrainees(new ArrayList<>());
        when(trainerRepository.findByUsernameWithTrainees("Arman.Nurpeisov")).thenReturn(Optional.of(trainer1));
        // when
        UpdateTrainerListDto dto = new UpdateTrainerListDto(username, List.of("Arman.Nurpeisov"));
        traineeService.updateTraineeTrainers(dto);
        // then
        assertThat(trainee.getTrainers()).contains(trainer1);
        verify(traineeRepository).save(trainee);
    }


    @Test
    void whenGettingAvailableTrainersForTrainee_shouldReturnUnassignedTrainers() {
        // given
        String username = "Bagdat.Serikbay";
        Trainee trainee = new Trainee();
        trainee.setId(60L);
        trainee.setUsername(username);

        Trainer assignedTrainer = new Trainer();
        assignedTrainer.setId(1L);
        assignedTrainer.setUsername("Anna.Borisova");
        assignedTrainer.setFirstName("Anna");
        assignedTrainer.setLastName("Borisova");

        Trainer unassignedTrainer = new Trainer();
        unassignedTrainer.setId(2L);
        unassignedTrainer.setUsername("Daniyar.Zhumagulov");
        unassignedTrainer.setFirstName("Daniyar");
        unassignedTrainer.setLastName("Zhumagulov");

        trainee.setTrainers(List.of(assignedTrainer));
        when(traineeRepository.findByUsernameWithTrainers(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(List.of(assignedTrainer, unassignedTrainer));
        when(converter.entityToShortDto(unassignedTrainer)).thenReturn(
                TrainerShortDto.builder()
                        .username(unassignedTrainer.getUsername())
                        .firstName(unassignedTrainer.getFirstName())
                        .lastName(unassignedTrainer.getLastName())
                        .specializationName(null)  // или нужное значение, если задано
                        .build()
        );
        when(authContextUtil.getUsername()).thenReturn(username);
        // when
        List<TrainerShortDto> result = traineeService.getAvailableTrainersForTrainee(username);
        // then
        assertThat(result).hasSize(1);
        TrainerShortDto trainer = result.getFirst();
        assertThat(trainer.getUsername()).isEqualTo("Daniyar.Zhumagulov");
        assertThat(trainer.getFirstName()).isEqualTo("Daniyar");
        assertThat(trainer.getLastName()).isEqualTo("Zhumagulov");
    }


    @Test
    void whenChangingTraineePassword_shouldUpdatePassword() {
        // given
        String username = "Dina.Aliyeva";
        String rawPassword = "newPass";
        String encodedPassword = "encodedPass123";
        Trainee trainee = new Trainee(10L, "Dina", "Aliyeva", username, "oldPass",
                true, LocalDate.of(1992, 3, 3), "Almaty", null, null);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        // when
        traineeService.setNewPassword(username, "newPass");
        // then
        assertThat(trainee.getPassword()).isEqualTo(encodedPassword);
        verify(traineeRepository).save(trainee);
    }
}