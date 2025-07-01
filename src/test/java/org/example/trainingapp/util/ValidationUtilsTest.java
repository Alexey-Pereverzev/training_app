package org.example.trainingapp.util;

import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.List;


class ValidationUtilsTest {

    @Test
    void whenValidateTraineeRegisterDto_valid_ok() {
        // given
        var dto = TraineeRegisterDto.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .address("Almaty")
                .build();
        // then
        ValidationUtils.validateTrainee(dto);   // do not throw
    }


    @Test
    void whenValidateTraineeRegisterDto_noFirstName_throw() {
        // given
        var dto = TraineeRegisterDto.builder()
                .lastName("Petrov")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainee(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name is required.");
    }


    @Test
    void whenValidateTraineeRequestDto_valid_ok() {
        // given
        var dto = TraineeRequestDto.builder()
                .username("Ivan.Petrov")
                .firstName("Ivan")
                .lastName("Petrov")
                .active(true)
                .address("Almaty")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .build();
        // then
        ValidationUtils.validateTrainee(dto);
    }


    @Test
    void whenValidateTraineeRequestDto_noActive_throw() {
        // given
        var dto = TraineeRequestDto.builder()
                .username("Ivan.Petrov")
                .firstName("Ivan")
                .lastName("Petrov")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainee(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Active status is required.");
    }

    @Test
    void whenValidateTraineeRequestDto_usernameDoesNotMatchFirstNameLastName_shouldThrowException() {
        // given
        TraineeRequestDto dto = TraineeRequestDto.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .username("Wrong.Username")
                .active(true)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainee(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username must start with 'Ivan.Petrov'");
    }

    @Test
    void whenValidateTraineeRequestDto_usernameStartsWithFirstNameLastName_ok() {
        // given
        TraineeRequestDto dto = TraineeRequestDto.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .username("Ivan.Petrov123")
                .active(true)
                .build();
        // then
        ValidationUtils.validateTrainee(dto);  // do not throw
    }


    @Test
    void whenValidateTrainerRegisterDto_valid_ok() {
        // given
        var dto = TrainerRegisterDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .build();
        // then
        ValidationUtils.validateTrainer(dto);   // do not throw
    }


    @Test
    void whenValidateTrainerRegisterDto_noSpecialization_throw() {
        // given
        var dto = TrainerRegisterDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specialization is required.");
    }


    @Test
    void whenValidateTrainerRequestDto_valid_ok() {
        // given
        var dto = TrainerRequestDto.builder()
                .username("Dina.Aliyeva")
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .active(true)
                .build();
        // then
        ValidationUtils.validateTrainer(dto);    // do not throw
    }


    @Test
    void whenValidateTrainerRequestDto_noUsername_throw() {
        // given
        var dto = TrainerRequestDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .active(true)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is required.");
    }


    @Test
    void whenValidateTrainingRequestDto_valid_ok() {
        // given
        var dto = TrainingRequestDto.builder()
                .name("Power Yoga")
                .duration(60)
                .date(LocalDate.now())
                .traineeName("Anna.Ivanova")
                .trainerName("Elena.Sokolova")
                .build();
        // then
        ValidationUtils.validateTraining(dto);    // do not throw
    }

    @Test
    void whenValidateTrainerRequestDto_usernameDoesNotMatchFirstNameLastName_shouldThrowException() {
        // given
        TrainerRequestDto dto = TrainerRequestDto.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .username("Wrong.Username")
                .specializationName("Yoga")
                .active(true)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username must start with 'Ivan.Petrov'");
    }

    @Test
    void whenValidateTrainerRequestDto_usernameStartsWithFirstNameLastName_ok() {
        // given
        TrainerRequestDto dto = TrainerRequestDto.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .username("Ivan.Petrov123")
                .specializationName("Yoga")
                .active(true)
                .build();
        // then
        ValidationUtils.validateTrainer(dto);  // do not throw
    }


    @Test
    void whenValidateTrainingRequestDto_durationZero_throw() {
        // given
        var dto = TrainingRequestDto.builder()
                .name("Power Yoga")
                .duration(0)
                .date(LocalDate.now())
                .traineeName("Anna.Ivanova")
                .trainerName("Elena.Sokolova")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTraining(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training duration must be positive.");
    }


    @Test
    void whenValidateCredentials_valid_ok() {
        // given
        var dto = CredentialsDto.builder()
                .username("user")
                .password("pw")
                .build();
        // then
        ValidationUtils.validateCredentials(dto);  // do not throw
    }


    @Test
    void whenValidateCredentials_noPassword_throw() {
        // given
        var dto = CredentialsDto.builder()
                .username("user")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateCredentials(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Password is required.");
    }


    @Test
    void whenValidateChangePasswordDto_valid_ok() {
        // given
        var dto = ChangePasswordDto.builder()
                .username("user")
                .oldPassword("old")
                .newPassword("new")
                .build();
        // then
        ValidationUtils.validateCredentials(dto);
    }


    @Test
    void whenValidateChangePasswordDto_missingNew_throw() {
        // given
        var dto = ChangePasswordDto.builder()
                .username("user")
                .oldPassword("old")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateCredentials(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("New password is required.");
    }


    @Test
    void whenValidateActiveStatus_valid_ok() {
        // given
        var dto = ActiveStatusDto.builder()
                .username("user")
                .active(true)
                .build();
        // then
        ValidationUtils.validateActiveStatus(dto);
    }


    @Test
    void whenValidateActiveStatus_nullActive_throw() {
        // given
        var dto = ActiveStatusDto.builder()
                .username("user")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateActiveStatus(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Active status is required.");
    }


    @Test
    void whenValidateUpdateTrainerList_valid_ok() {
        // given
        var dto = new UpdateTrainerListDto(
                "trainee.username",
                List.of("Trainer.One", "Trainer.Two")
        );
        // then
        ValidationUtils.validateUpdateTrainerList(dto);
    }


    @Test
    void whenValidateUpdateTrainerList_emptyList_throw() {
        // given
        var dto = new UpdateTrainerListDto("trainee.username", List.of());
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateUpdateTrainerList(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one trainer must be provided.");
    }


    @Test
    void whenValidateUsername_blank_throw() {
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateUsername(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is required.");
    }


    @Test
    void whenValidateUsername_ok() {
        // then
        ValidationUtils.validateUsername("Good.Username");
    }

}

