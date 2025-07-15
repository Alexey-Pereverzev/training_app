package org.example.trainingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.metrics.RegistrationMetrics;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.UserRepository;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Converter converter;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RegistrationMetrics registrationMetrics;


    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void whenChangingPassword_forTrainer_shouldCallTrainerService() {
        // given
        ChangePasswordDto dto = new ChangePasswordDto("Elena.Sokolova", "oldPass", "newPass");
        when(authenticationService.getRole("Elena.Sokolova", "oldPass")).thenReturn(Role.TRAINER);
        // when
        userService.changePassword(dto);
        // then
        verify(trainerService).setNewPassword("Elena.Sokolova", "newPass");
        verifyNoInteractions(traineeService);
    }


    @Test
    void whenChangingPassword_forTrainee_shouldCallTraineeService() {
        // given
        ChangePasswordDto dto = new ChangePasswordDto("Anna.Ivanova", "123", "456");
        when(authenticationService.getRole("Anna.Ivanova", "123")).thenReturn(Role.TRAINEE);
        // when
        userService.changePassword(dto);
        // then
        verify(traineeService).setNewPassword("Anna.Ivanova", "456");
        verifyNoInteractions(trainerService);
    }


    @Test
    void whenCreatingTrainee_shouldGenerateUsernameAndPassword() {
        // given
        TraineeRegisterDto traineeDto = TraineeRegisterDto.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Almaty")
                .build();
        Trainee traineeEntity = Trainee.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Almaty")
                .build();
        when(converter.dtoToEntity(traineeDto)).thenReturn(traineeEntity);
        when(userRepository.findUsernamesByFirstNameAndLastName("Ivan", "Petrov")).thenReturn(Set.of());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded123");
        // when
        userService.createTrainee(traineeDto);
        // then
        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).save(captor.capture());
        verify(registrationMetrics).incrementTrainee();
        Trainee saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("Ivan.Petrov");
        assertThat(saved.getPassword()).isNotNull();
        assertThat(saved.getPassword().length()).isEqualTo(10);
        assertThat(saved.isActive()).isTrue();
    }


    @Test
    void whenCreatingTrainer_shouldGenerateUsernameAndPassword() {
        // given
        TrainerRegisterDto dto = TrainerRegisterDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .build();
        TrainingType yoga = new TrainingType("Yoga");
        Trainer entity = Trainer.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specialization(yoga)
                .build();
        when(converter.dtoToEntity(dto)).thenReturn(entity);
        when(userRepository.findUsernamesByFirstNameAndLastName("Dina", "Aliyeva")).thenReturn(Set.of());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded123");
        // when
        CredentialsDto creds = userService.createTrainer(dto);
        // then
        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(captor.capture());
        verify(registrationMetrics).incrementTrainer();
        Trainer saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("Dina.Aliyeva");
        assertThat(saved.getPassword()).isEqualTo("encoded123");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getSpecialization().getName()).isEqualTo("Yoga");
        verify(passwordEncoder).encode(creds.getPassword());
        assertThat(creds.getPassword()).hasSize(10);
        assertThat(creds.getUsername()).isEqualTo("Dina.Aliyeva");
    }


    @Test
    void whenChangingPassword_withInvalidCredentials_shouldThrow() {
        // given
        ChangePasswordDto dto = new ChangePasswordDto("", "", "");
        // when + then
        assertThatThrownBy(() -> userService.changePassword(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
        verifyNoInteractions(trainerService, traineeService, authenticationService);
    }


    @Test
    void whenCreatingTrainer_andConverterFails_shouldThrowRuntimeException() {
        // given
        TrainerRegisterDto dto = TrainerRegisterDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("NonExistentType")
                .build();
        when(converter.dtoToEntity(dto)).thenThrow(new EntityNotFoundException("Training type not found"));
        // when + then
        assertThatThrownBy(() -> userService.createTrainer(dto))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Training type not found");
        verifyNoInteractions(userRepository, trainerRepository, registrationMetrics);
    }
}
