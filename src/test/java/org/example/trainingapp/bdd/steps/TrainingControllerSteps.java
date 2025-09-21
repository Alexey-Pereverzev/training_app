package org.example.trainingapp.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.bdd.TestContext;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingRepository;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RequiredArgsConstructor
public class TrainingControllerSteps {

    private final TestContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;


    // Given the application test context and authentication are prepared - defined in CommonHttpSteps


    // Given a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And a trainee "Anna.Ivanova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I create training "Power Yoga" for trainee "Anna.Ivanova" with duration 60 minutes
    @When("I create training {string} for trainee {string} with duration {int} minutes")
    public void createTrainingForTraineeWithDuration(String trainingName, String traineeUsername, int minutes) {
        String trainerUsername = context.getAuthUser() != null ? context.getAuthUser() : "Elena.Sokolova";
        ensureTrainer(trainerUsername);
        ensureTrainee(traineeUsername);
        TrainingRequestDto dto = TrainingRequestDto.builder()
                .name(trainingName)
                .date(LocalDate.now().plusDays(1))
                .duration(minutes)
                .trainerName(trainerUsername)
                .traineeName(traineeUsername)
                .build();
        try {
            var mvcRes = mockMvc.perform(
                    post("/api/trainings")
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 201 - defined in CommonHttpSteps
    // And the response body should contain "created successfully" - defined in CommonHttpSteps
    // And a JMS event should be published - defined in JmsThenSteps


    // Given a trainee "Anna.Ivanova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Unknown" with role "TRAINER" - defined in CommonHttpSteps

    // When I create training "Boxing" for trainee "Anna.Ivanova" with trainer "Unknown"
    // When I create training "Morning Yoga" for trainee "Anna.Ivanova" with trainer "Elena.Sokolova"
    @When("I create training {string} for trainee {string} with trainer {string}")
    public void createTrainingForTraineeWithExplicitTrainer(String trainingName, String traineeUsername, String trainerUsername) {
        ensureTrainee(traineeUsername);
        TrainingRequestDto dto = TrainingRequestDto.builder()
                .name(trainingName)
                .date(LocalDate.now().plusDays(1))
                .duration(60)
                .trainerName(trainerUsername)
                .traineeName(traineeUsername)
                .build();
        try {
            var mvcRes = mockMvc.perform(
                    post("/api/trainings")
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 404 - defined in CommonHttpSteps
    // And the response body should contain "Trainer not found" - defined in CommonHttpSteps


    // Given a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And a trainee "Anna.Ivanova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Other.Coach" with role "TRAINER" - defined in CommonHttpSteps
    // When I create training "Morning Yoga" for trainee "Anna.Ivanova" with trainer "Elena.Sokolova" - defined above
    // Then response status should be 403 - defined in CommonHttpSteps


    // Given a training "Morning Yoga" exists
    @Given("a training {string} exists")
    public void aTrainingExistsFuture(String name) {
        TrainingType type = ensureType();
        Trainer trainer = ensureTrainer("Elena.Sokolova");
        Trainee trainee = ensureTrainee("Anna.Ivanova");
        Training training = Training.builder()
                .trainingName(name)
                .trainingDate(LocalDate.now().plusDays(2))
                .trainingDuration(60)
                .trainingType(type)
                .trainer(trainer)
                .trainee(trainee)
                .build();
        trainingRepository.save(training);
        context.setLastTraining(training);
    }

    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I delete training "Morning Yoga"
    // When I delete training "Non.Existing"
    // When I delete training "Old Yoga"
    @When("I delete training {string}")
    public void deleteTraining(String trainingName) {
        try {
            var mvcRes = mockMvc.perform(
                    delete("/api/trainings/{name}", trainingName)
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 200 - defined in CommonHttpSteps
    // And the response body should contain "deleted successfully" - defined in CommonHttpSteps
    // And a JMS event should be published - defined in JmsThenSteps


    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps
    // When I delete training "Non.Existing" - defined above
    // Then response status should be 404 - defined in CommonHttpSteps
    // And the response body should contain "Not Found" - defined in CommonHttpSteps


    // Given a past training named "Old Yoga" exists in DB
    @Given("a past training named {string} exists in DB")
    public void aPastTrainingExists(String name) {
        TrainingType type = ensureType();
        Trainer trainer = ensureTrainer("Elena.Sokolova");
        Trainee trainee = ensureTrainee("Anna.Ivanova");
        Training training = Training.builder()
                .trainingName(name)
                .trainingDate(LocalDate.now().minusDays(2))
                .trainingDuration(60)
                .trainingType(type)
                .trainer(trainer)
                .trainee(trainee)
                .build();
        trainingRepository.save(training);
        context.setLastTraining(training);
    }

    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps
    // When I delete training "Old Yoga" - defined above
    // Then response status should be 409 - defined in CommonHttpSteps
    // And the response body should contain "Deleting past trainings prohibited" - defined in CommonHttpSteps


    // Given a training "Morning Yoga" exists - defined above
    // And I am not authenticated - defined in CommonHttpSteps
    // When I delete training "Morning Yoga" - defined above
    // Then response status should be 401 - defined in CommonHttpSteps


    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I trigger training hours sync
    @When("I trigger training hours sync")
    public void triggerTrainingHoursSync() {
        try {
            var mvcRes = mockMvc.perform(
                    post("/api/trainings/sync-hours")
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 200 - defined in CommonHttpSteps
    // And the response body should contain "txId" - defined in CommonHttpSteps


    // And I am authenticated as "Ivan.Petrov" with role "TRAINEE" - defined in CommonHttpSteps
    // When I trigger training hours sync - defined above
    // Then response status should be 403 - defined in CommonHttpSteps


    private TrainingType ensureType() {
        return trainingTypeRepository.findByName("Yoga")
                .orElseGet(() -> trainingTypeRepository.save(new TrainingType("Yoga")));
    }

    private Trainer ensureTrainer(String username) {
        return trainerRepository.findByUsername(username).orElseGet(() -> {
            String[] n = username.split("\\.");
            TrainingType type = ensureType();
            return trainerRepository.save(Trainer.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password"))
                    .firstName(n[0])
                    .lastName(n[1])
                    .active(true)
                    .specialization(type)
                    .trainings(new ArrayList<>())
                    .trainees(new ArrayList<>())
                    .build());
        });
    }

    private Trainee ensureTrainee(String username) {
        return traineeRepository.findByUsername(username).orElseGet(() -> {
            String[] n = username.split("\\.");
            return traineeRepository.save(Trainee.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password"))
                    .firstName(n[0])
                    .lastName(n[1])
                    .active(true)
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .trainings(new ArrayList<>())
                    .trainers(new ArrayList<>())
                    .build());
        });
    }

}
