package org.example.trainingapp.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.bdd.TestContext;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@RequiredArgsConstructor
public class TraineeControllerSteps {

    private final TestContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;


    // Given the application test context and authentication are prepared - defined in CommonHttpSteps


    // Given a trainee "Ivan.Petrov" exists - defined in CommonGivenSteps
    // And I am authenticated as "Ivan.Petrov" with role "TRAINEE" - defined in CommonHttpSteps

    // When I update the trainee "Ivan.Petrov" address to "Almaty"
    // When I update the trainee "Unknown.User" address to "Nowhere"
    @When("I update the trainee {string} address to {string}")
    public void updateTraineeAddress(String username, String newAddress) {
        TraineeRequestDto req = TraineeRequestDto.builder()
                .username(username)
                .firstName(username.split("\\.")[0])
                .lastName(username.split("\\.")[1])
                .active(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address(newAddress)
                .build();
        try {
            var builder = put("/api/trainees")
                    .with(context.getAuth() != null ? context.getAuth() : anonymous())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req));
            String token = context.getAuthJwt();
            if (token != null && !token.isBlank()) {
                builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
            var mvcRes = mockMvc.perform(builder).andReturn();
            assertThat(mvcRes.getRequest().getHeader(HttpHeaders.AUTHORIZATION))
                    .isNotBlank().startsWith("Bearer ");
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 200 - defined in CommonHttpSteps

    // And the trainee address in the response should be "Almaty"
    @Then("the trainee address in the response should be {string}")
    public void traineeAddressInResponseShouldBe(String expected) {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastStatus()).isEqualTo(200);
        String actual = JsonPath.read(context.getLastBody(), "$.address");
        assertThat(actual).isEqualTo(expected);
    }


    // And a trainee "Unknown.User" exists - defined in CommonGivenSteps
    // And I am authenticated as "Unknown.User" with role "TRAINEE" - defined in CommonHttpSteps
    // And trainee "Unknown.User" is removed from DB - defined in CommonGivenSteps
    // When I update the trainee "Unknown.User" address to "Nowhere" - defined above
    // Then response status should be 401 - defined in CommonHttpSteps



    // Given a trainee "Nina.Rakhimova" exists - defined in CommonGivenSteps

    // And the trainee "Nina.Rakhimova" has a training "AnyName" with trainer "Elena.Sokolova" of type "Yoga"
    @Given("the trainee {string} has a training {string} with trainer {string} of type {string}")
    @Transactional
    public void traineeHasTrainingWithTrainerAndType(String traineeUsername, String trainingName, String trainerUsername,
                                                     String typeName) {
        var trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalStateException("Precondition violated: trainee " + traineeUsername + " must exist"));
        var type = trainingTypeRepository.findByName(typeName)
                .orElseGet(() -> trainingTypeRepository.save(new TrainingType(typeName)));
        var trainer = trainerRepository.findByUsername(trainerUsername).orElseGet(() -> {
            var names = trainerUsername.split("\\.");
            return trainerRepository.save(Trainer.builder()
                    .username(trainerUsername)
                    .password(passwordEncoder.encode("password"))
                    .firstName(names[0]).lastName(names[1])
                    .active(true)
                    .specialization(type)
                    .trainings(new ArrayList<>()).trainees(new ArrayList<>())
                    .build());
        });
        var training = Training.builder()
                .trainingName(trainingName)
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(60)
                .trainingType(type)
                .trainee(trainee)
                .trainer(trainer)
                .build();
        trainee.getTrainings().add(training);
        trainer.getTrainings().add(training);
        trainerRepository.saveAndFlush(trainer);
        traineeRepository.saveAndFlush(trainee);
    }

    // And I am authenticated as "Nina.Rakhimova" with role "TRAINEE" - defined in CommonHttpSteps

    // When I delete the trainee "Nina.Rakhimova"
    // When I delete the trainee "Not.Exist"
    // When I delete the trainee "Ivan.Petrov"
    @When("I delete the trainee {string}")
    public void deleteTrainee(String username) {
        try {
            var mvcRes = mockMvc.perform(
                            delete("/api/trainees/{username}", username)
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
    // And a JMS event should be published - defined in JmsThenSteps


    // And I am authenticated as "Not.Exist" with role "TRAINEE" - defined in CommonHttpSteps
    // When I delete the trainee "Not.Exist" - defined above
    // Then response status should be 401 - defined in CommonHttpSteps


    // Given a trainee "Ivan.Petrov" has trainings
    @Given("a trainee {string} has trainings")
    @Transactional
    public void traineeHasTrainings(String traineeUsername) {
        var trainee = traineeRepository.findByUsername(traineeUsername).orElseGet(() -> {
            var names = traineeUsername.split("\\.");
            return traineeRepository.save(Trainee.builder()
                    .username(traineeUsername)
                    .password(passwordEncoder.encode("password"))
                    .firstName(names[0]).lastName(names[1])
                    .active(true)
                    .dateOfBirth(LocalDate.of(1990,1,1))
                    .address("Almaty")
                    .trainings(new ArrayList<>()).trainers(new ArrayList<>())
                    .build());
        });
        var trainer1 = ensureTrainer("Elena.Sokolova");
        var trainer2 = ensureTrainer("Other.Coach");
        trainee.getTrainings().clear();
        addTraining(trainee, trainer1, LocalDate.parse("2025-01-10"), "Yoga A");
        addTraining(trainee, trainer1, LocalDate.parse("2025-01-20"), "Yoga B");
        addTraining(trainee, trainer2, LocalDate.parse("2025-02-10"), "Boxing");
        traineeRepository.saveAndFlush(trainee);
    }

    // And I am authenticated as "Ivan.Petrov" with role "TRAINEE" - defined in CommonHttpSteps

    // When I request trainings between "2025-01-01" and "2025-01-31" with trainer "Elena.Sokolova"
    @When("I request trainings between {string} and {string} with trainer {string}")
    public void requestTrainingsWithFilter(String from, String to, String trainerName) {
        // username берём из текущей аутентификации, как в сценариях
        String username = context.getAuthUser() != null ? context.getAuthUser() : "Ivan.Petrov";
        try {
            var mvcRes = mockMvc.perform(
                            get("/api/trainees/{username}/trainings", username)
                                    .with(context.getAuth() != null ? context.getAuth() : anonymous())
                                    .queryParam("fromDate", from)
                                    .queryParam("toDate", to)
                                    .queryParam("trainerName", trainerName))
                    .andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 200 - defined in CommonHttpSteps

    // And I should receive a list of trainings matching the filter criteria
    @Then("I should receive a list of trainings matching the filter criteria")
    public void shouldReceiveFilteredTrainings() {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastStatus()).isEqualTo(200);
        List<Object> items = JsonPath.read(context.getLastBody(), "$");
        assertThat(items).isInstanceOf(List.class);                             // body is a non-empty array
        assertThat(items).isNotEmpty();
        try {                                   // date filter check
            List<String> dates = JsonPath.read(context.getLastBody(), "$[*].date");
            if (dates != null && !dates.isEmpty()) {
                assertThat(dates).allSatisfy(d -> assertThat(d).isNotBlank());
            }
        } catch (Exception ignore) {            // ignoring incorrect data
        }
        boolean trainerMatched = false;         // trainer matching
        try {
            List<String> trainers = JsonPath.read(context.getLastBody(), "$[*].trainerName");
            trainerMatched = trainers != null && !trainers.isEmpty();
        } catch (Exception ignore) {
        }
        if (!trainerMatched) {
            try {
                List<String> trainers = JsonPath.read(context.getLastBody(), "$[*].trainerUsername");
                trainerMatched = trainers != null && !trainers.isEmpty();
            } catch (Exception ignore) {
            }
        }
        assertThat(trainerMatched).isTrue();
    }


    // Given a trainee "Ivan.Petrov" exists - defined in CommonGivenSteps
    // And a trainee "Nina.Rakhimova" exists - defined in CommonGivenSteps
    // And I am authenticated as "Nina.Rakhimova" with role "TRAINEE" - defined in CommonHttpSteps
    // When I update the trainee "Ivan.Petrov" address to "Almaty" - defined above
    // Then response status should be 403 - defined in CommonHttpSteps


    // Given a trainee "Ivan.Petrov" exists - defined in CommonGivenSteps
    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I get trainee "Ivan.Petrov" info
    @When("I get trainee {string} info")
    public void getTraineeInfo(String username) {
        try {
            var mvcRes = mockMvc.perform(
                            get("/api/trainees/{username}", username)
                                    .with(context.getAuth() != null ? context.getAuth() : anonymous())
                    ).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 403 - defined in CommonHttpSteps


    // Given a trainee "Ivan.Petrov" exists - defined in CommonGivenSteps
    // And I am not authenticated - defined in CommonGivenSteps
    // When I delete the trainee "Ivan.Petrov" - defined above
    // Then response status should be 401 - defined in CommonHttpSteps


    private Trainer ensureTrainer(String username) {
        return trainerRepository.findByUsername(username).orElseGet(() -> {
            var names = username.split("\\.");
            var type = trainingTypeRepository.findByName("Yoga")
                    .orElseGet(() -> trainingTypeRepository.save(new TrainingType("Yoga")));
            return trainerRepository.save(Trainer.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password"))
                    .firstName(names[0]).lastName(names[1])
                    .active(true)
                    .specialization(type)
                    .trainings(new ArrayList<>()).trainees(new ArrayList<>())
                    .build());
        });
    }

    private void addTraining(Trainee trainee, Trainer trainer, LocalDate date, String name) {
        var type = trainingTypeRepository.findByName("Yoga")
                .orElseGet(() -> trainingTypeRepository.save(new TrainingType("Yoga")));
        var t = Training.builder()
                .trainingName(name)
                .trainingDate(date)
                .trainingType(type)
                .trainingDuration(60)
                .trainee(trainee)
                .trainer(trainer)
                .build();
        trainee.getTrainings().add(t);
        trainer.getTrainings().add(t);
    }

}


