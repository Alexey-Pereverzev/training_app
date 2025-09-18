package org.example.trainingapp.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.bdd.TestContext;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@RequiredArgsConstructor
public class TrainerControllerSteps {

    private final TestContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;


    // Given the application test context and authentication are prepared - defined in CommonHttpSteps


    // Given a trainer "Dina.Aliyeva" exists - defined in CommonGivenSteps
    // And I am authenticated as "Dina.Aliyeva" with role "TRAINER" - defined in CommonHttpSteps

    // When I update the trainer "Dina.Aliyeva" specialization to "Pilates"
    @When("I update the trainer {string} specialization to {string}")
    public void updateTrainerSpecViaApi(String username, String specialization) {
        trainingTypeRepository.findByName(specialization)
                .orElseGet(() -> trainingTypeRepository.save(new TrainingType(specialization)));
        TrainerRequestDto req = TrainerRequestDto.builder()
                .username(username)
                .firstName(username.split("\\.")[0])
                .lastName(username.split("\\.")[1])
                .specializationName(specialization)
                .active(true)
                .build();
        try {
            var mvcRes = mockMvc.perform(
                    put("/api/trainers")
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 200 - defined in CommonHttpSteps

    // And the trainer specialization in the response should be "Pilates"
    @Then("the trainer specialization in the response should be {string}")
    public void trainerSpecializationInResponseShouldBe(String expected) {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastStatus()).isEqualTo(200);
        String actual = null;       // parse dto as specializationName or specialization.name
        try {
            actual = JsonPath.read(context.getLastBody(), "$.specializationName");
        } catch (Exception ignored) {}
        if (actual == null) {
            try {
                actual = JsonPath.read(context.getLastBody(), "$.specialization.name");
            } catch (Exception ignored) {}
        }
        assertThat(actual).isEqualTo(expected);
    }


    // Given a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I update the trainer "Elena.Sokolova" without specialization
    @When("I update the trainer {string} without specialization")
    public void updateTrainerWithoutSpecViaApi(String username) {
        TrainerRequestDto req = TrainerRequestDto.builder()
                .username(username)
                .firstName(username.split("\\.")[0])
                .lastName(username.split("\\.")[1])
                .active(true)
                .build();           // no specialization in dto
        try {
            var mvcRes = mockMvc.perform(
                    put("/api/trainers")
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 400 - defined in CommonHttpSteps
    // And the response body should contain "Specialization is required." - defined in CommonHttpSteps


    // Given a trainer "Dina.Aliyeva" exists - defined in CommonGivenSteps
    // And a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps
    // When I update the trainer "Dina.Aliyeva" specialization to "Pilates" - defined above
    // Then response status should be 403 - defined in CommonHttpSteps


    // Given a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I get trainer "Elena.Sokolova" info
    // When I get trainer "NotExist" info
    @When("I get trainer {string} info")
    public void getTrainerInfo(String username) {
        try {
            var mvcRes = mockMvc.perform(
                    get("/api/trainers/{username}", username)
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


    // Given a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Ivan.Petrov" with role "TRAINEE" - defined in CommonHttpSteps
    // When I get trainer "Elena.Sokolova" info - defined above
    // Then response status should be 403 - defined in CommonHttpSteps


    // And I am authenticated as "NotExist" with role "TRAINER" - defined in CommonHttpSteps
    // When I get trainer "NotExist" info - defined above
    // And trainer "NotExist" is removed from DB
    // Then response status should be 404 - defined in CommonHttpSteps
    // And the response body should contain "Not Found" - defined in CommonHttpSteps


    // Given a trainer "Elena.Sokolova" has trainings
    @Given("a trainer {string} has trainings")
    @Transactional
    public void trainerHasTrainings(String username) {
        Trainer trainer = ensureTrainer(username);
        TrainingType type = trainingTypeRepository.findByName("Yoga")
                .orElseGet(() -> trainingTypeRepository.save(new TrainingType("Yoga")));
        Trainee trainee = traineeRepository.findByUsername("Test.Trainee").orElseGet(() -> {
            String u = "Test.Trainee";
            String[] n = u.split("\\.");
            return traineeRepository.save(Trainee.builder()
                    .username(u)
                    .password(passwordEncoder.encode("password"))
                    .firstName(n[0])
                    .lastName(n[1])
                    .active(true)
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .trainers(new ArrayList<>())
                    .trainings(new ArrayList<>())
                    .build());
        });
        trainer.getTrainings().clear();
        addTraining(trainer, trainee, LocalDate.of(2025, 2, 5), "T1", type);
        addTraining(trainer, trainee, LocalDate.of(2025, 2, 10), "T2", type);
        addTraining(trainer, trainee, LocalDate.of(2025, 2, 15), "T3", type);
        trainerRepository.saveAndFlush(trainer);
        context.setLastTrainer(trainer);
    }

    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I request trainer trainings between "2025-02-01" and "2025-02-28"
    @When("I request trainer trainings between {string} and {string}")
    public void requestTrainerTrainingsForCurrentUser(String from, String to) {
        String username = context.getAuthUser() != null ? context.getAuthUser() : "Elena.Sokolova";
        try {
            var mvcRes = mockMvc.perform(
                    get("/api/trainers/{username}/trainings", username)
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
                            .queryParam("fromDate", from)
                            .queryParam("toDate", to)
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 200 - defined in CommonHttpSteps

    // And I should receive a list of trainings in that range
    @Then("I should receive a list of trainings in that range")
    public void shouldReceiveTrainingsInRange() {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastStatus()).isEqualTo(200);
        List<?> items = JsonPath.read(context.getLastBody(), "$");
        assertThat(items).isInstanceOf(List.class);
        assertThat(items).isNotEmpty();
        var from = LocalDate.of(2025, 2, 1);
        var to   = LocalDate.of(2025, 2, 28);
        List<String> dates = JsonPath.read(context.getLastBody(), "$[*].date");
        assertThat(dates).isNotNull().isNotEmpty();
        for (String d : dates) {
            LocalDate ld = LocalDate.parse(d);
            assertThat(ld).isAfterOrEqualTo(from);
            assertThat(ld).isBeforeOrEqualTo(to);
        }
    }


    // Given a trainer "Elena.Sokolova" has trainings - defined above
    // And I am not authenticated - defined in CommonHttpSteps

    // When I request trainer trainings for "Elena.Sokolova" between "2025-02-01" and "2025-02-28"
    @When("I request trainer trainings for {string} between {string} and {string}")
    public void requestTrainerTrainingsForUser(String username, String from, String to) {
        try {
            var mvcRes = mockMvc.perform(
                    get("/api/trainers/{username}/trainings", username)
                            .with(context.getAuth() != null ? context.getAuth() : anonymous())
                            .queryParam("fromDate", from)
                            .queryParam("toDate", to)
            ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 401 - defined in CommonHttpSteps


    // Given a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I change trainer "Elena.Sokolova" active status to "true"
    // When I change trainer "Elena.Sokolova" active status to "false"
    @When("I change trainer {string} active status to {string}")
    public void changeTrainerActiveStatus(String username, String activeStr) {
        boolean active = Boolean.parseBoolean(activeStr);
        ActiveStatusDto dto = new ActiveStatusDto(username, active);
        try {
            var mvcRes = mockMvc.perform(
                    patch("/api/trainers/active-status")
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

    // Then response status should be 200 - defined in CommonHttpSteps
    // And the response body should contain "Trainer active status changed to true" - defined in CommonHttpSteps


    // Given a trainer "Elena.Sokolova" exists - defined in CommonGivenSteps
    // And I am not authenticated - defined in CommonHttpSteps
    // When I change trainer "Elena.Sokolova" active status to "false" - defined above
    // Then response status should be 401 - defined in CommonHttpSteps


    private Trainer ensureTrainer(String username) {
        return trainerRepository.findByUsername(username).orElseGet(() -> {
            String[] names = username.split("\\.");
            TrainingType type = trainingTypeRepository.findByName("Yoga")
                    .orElseGet(() -> trainingTypeRepository.save(new TrainingType("Yoga")));
            return trainerRepository.save(Trainer.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password"))
                    .firstName(names[0])
                    .lastName(names[1])
                    .active(true)
                    .specialization(type)
                    .trainings(new ArrayList<>())
                    .trainees(new ArrayList<>())
                    .build());
        });
    }

    private void addTraining(Trainer trainer, Trainee trainee, LocalDate date, String name, TrainingType type) {
        trainer.getTrainings().add(Training.builder()
                .trainingName(name)
                .trainingDate(date)
                .trainingDuration(60)
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(type)
                .build());
    }

}


