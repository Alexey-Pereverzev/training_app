package org.example.trainingapp.util;

import org.example.trainingapp.dto.TraineeDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class ValidationUtilsTest {

    // tests for validateTrainee()

    @Test
    void whenValidData_validateTrainee_shouldPass() {
        // given
        TraineeDto trainee = TraineeDto.builder()
                .firstName("Ivan")
                .lastName("Petrov")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Almaty")
                .build();
        // when + then
        ValidationUtils.validateTrainee(trainee); // should not throw exception
    }

    @Test
    void whenFirstNameIsNullOrBlank_validateTrainee_shouldThrow() {
        // given
        TraineeDto trainee1 = TraineeDto.builder()
                .firstName(null)
                .lastName("Petrov")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Almaty")
                .build();
        TraineeDto trainee2 = TraineeDto.builder()
                .firstName("  ")
                .lastName("Petrov")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Almaty")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainee(trainee1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name is required.");
        assertThatThrownBy(() -> ValidationUtils.validateTrainee(trainee2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name is required.");
    }

    @Test
    void whenLastNameIsNullOrBlank_validateTrainee_shouldThrow() {
        // given
        TraineeDto trainee = TraineeDto.builder()
                .firstName("Ivan")
                .lastName(" ")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Almaty")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainee(trainee))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name is required.");
    }


    // tests for validateTrainer()

    @Test
    void whenValidData_validateTrainer_shouldPass() {
        // given
        TrainerDto trainer = TrainerDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .build();
        // when + then
        ValidationUtils.validateTrainer(trainer);
    }

    @Test
    void whenFirstNameIsNullOrBlank_validateTrainer_shouldThrow() {
        // given
        TrainerDto trainer = TrainerDto.builder()
                .firstName(" ")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainer(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("First name is required.");
    }

    @Test
    void whenLastNameIsNullOrBlank_validateTrainer_shouldThrow() {
        // given
        TrainerDto trainer = TrainerDto.builder()
                .firstName("Dina")
                .lastName("")
                .specializationName("Yoga")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainer(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Last name is required.");
    }

    @Test
    void whenSpecializationIsNullOrBlank_validateTrainer_shouldThrow() {
        // given
        TrainerDto trainer = TrainerDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName(" ")
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTrainer(trainer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specialization is required.");
    }


    // tests for validateTraining()

    @Test
    void whenValidData_validateTraining_shouldPass() {
        // given
        TrainingDto training = TrainingDto.builder()
                .trainingName("Yoga Session")
                .trainingDuration(60)
                .trainingDate(LocalDate.now())
                .trainingType("Yoga")
                .traineeId(1L)
                .trainerId(2L)
                .build();
        // when + then
        ValidationUtils.validateTraining(training);
    }

    @Test
    void whenTrainingNameIsNullOrBlank_validateTraining_shouldThrow() {
        // given
        TrainingDto training = TrainingDto.builder()
                .trainingName("")
                .trainingDuration(60)
                .trainingDate(LocalDate.now())
                .trainingType("Yoga")
                .traineeId(1L)
                .trainerId(2L)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTraining(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training name is required.");
    }

    @Test
    void whenTrainingDurationIsInvalid_validateTraining_shouldThrow() {
        // given
        TrainingDto training = TrainingDto.builder()
                .trainingName("Yoga Session")
                .trainingDuration(0)
                .trainingDate(LocalDate.now())
                .trainingType("Yoga")
                .traineeId(1L)
                .trainerId(2L)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTraining(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training duration must be positive.");
    }

    @Test
    void whenTrainingDateIsNull_validateTraining_shouldThrow() {
        // given
        TrainingDto training = TrainingDto.builder()
                .trainingName("Yoga Session")
                .trainingDuration(60)
                .trainingType("Yoga")
                .traineeId(1L)
                .trainerId(2L)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTraining(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training date is required.");
    }

    @Test
    void whenTrainingTypeIsNullOrBlank_validateTraining_shouldThrow() {
        // given
        TrainingDto training = TrainingDto.builder()
                .trainingName("Yoga Session")
                .trainingDuration(60)
                .trainingDate(LocalDate.now())
                .traineeId(1L)
                .trainerId(2L)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTraining(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training type is required.");
    }

    @Test
    void whenTraineeIdIsNull_validateTraining_shouldThrow() {
        // given
        TrainingDto training = TrainingDto.builder()
                .trainingName("Yoga Session")
                .trainingDuration(60)
                .trainingDate(LocalDate.now())
                .trainingType("Yoga")
                .trainerId(2L)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTraining(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee id is required.");
    }

    @Test
    void whenTrainerIdIsNull_validateTraining_shouldThrow() {
        // given
        TrainingDto training = TrainingDto.builder()
                .trainingName("Yoga Session")
                .trainingDuration(60)
                .trainingDate(LocalDate.now())
                .trainingType("Yoga")
                .traineeId(1L)
                .build();
        // when + then
        assertThatThrownBy(() -> ValidationUtils.validateTraining(training))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer id is required.");
    }
}
