package org.example.trainingapp.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.bdd.TestContext;
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.example.trainingapp.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.trainingapp.constant.Constant.BEARER;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;



@RequiredArgsConstructor
public class UserControllerSteps {

    private final TestContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;


    // Given the application test context and authentication are prepared - defined in CommonHttpSteps

    
    // Given a user "trainee1" with password "pw123" exists - defined in CommonGivenSteps

    // When I authorize with username "trainee1" and password "pw123"
    // When I authorize with username "trainee1" and password "wrong"
    // When I authorize with username "" and password "pw123"
    @When("I authorize with username {string} and password {string}")
    public void authorize(String username, String rawPassword) {
        try {
            CredentialsDto dto = new CredentialsDto(username, rawPassword);
            var mvcRes = mockMvc.perform(
                    post("/api/users/login")
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

    // And I should receive a valid JWT token
    @Then("I should receive a valid JWT token")
    public void shouldReceiveValidJwt() {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastStatus()).isEqualTo(200);
        String token = null;
        try { token = JsonPath.read(context.getLastBody(), "$.token"); } catch (Exception ignored) {}
        assertThat(token).as("JWT token expected").isNotBlank();
    }

    
    // Given a user "trainee1" with password "pw123" exists - defined in CommonGivenSteps
    // When I authorize with username "trainee1" and password "wrong" - defined above
    // Then response status should be 401 - defined in CommonHttpSteps

    
    // Given a user "trainer1" with role "ROLE_TRAINER" exists - defined in CommonGivenSteps

    // When I request role for username "trainer1" and password "pw123"
    @When("I request role for username {string} and password {string}")
    public void requestRole(String username, String rawPassword) {
        authorize(username, rawPassword);
    }

    // Then response status should be 200 - defined in CommonHttpSteps

    // And I should get role "ROLE_TRAINER"
    @Then("I should get role {string}")
    public void shouldGetRole(String expected) {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastStatus()).isEqualTo(200);
        String role = JsonPath.read(context.getLastBody(), "$.role");
        String normalized = role != null && role.startsWith("ROLE_") ? role : "ROLE_" + role;
        assertThat(normalized).isEqualTo(expected);
    }

    
    // When I authorize with username "" and password "pw123" - defined above
    // Then response status should be 400 - defined in CommonHttpSteps

    
    // When I register trainee with first name "Ivan" and last name "Petrov"
    @When("I register trainee with first name {string} and last name {string}")
    public void registerTrainee(String first, String last) {
        try {
            ensureUsernameBaseFree(first + "." + last);             // no namesakes in DB
            TraineeRegisterDto dto = TraineeRegisterDto.builder()
                    .firstName(first)
                    .lastName(last)
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .address("Almaty")
                    .build();
            var mvcRes = mockMvc.perform(
                    post("/api/users/register-trainee")
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
    // And the response body should contain "Ivan.Petrov" - defined in CommonHttpSteps

    
    // When I register trainer with first name "Dina" and last name "Aliyeva" specialization "Yoga"
    @When("I register trainer with first name {string} and last name {string} specialization {string}")
    public void registerTrainer(String first, String last, String specialization) {
        try {
            trainingTypeRepository.findByName(specialization)
                    .orElseGet(() -> trainingTypeRepository.save(new TrainingType(specialization)));
            TrainerRegisterDto dto = TrainerRegisterDto.builder()
                    .firstName(first)
                    .lastName(last)
                    .specializationName(specialization)
                    .build();
            var mvcRes = mockMvc.perform(
                    post("/api/users/register-trainer")
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
    // And the response body should contain "Dina.Aliyeva" - defined in CommonHttpSteps

    
    // Given a user "trainee1" with password "old" exists - defined in CommonGivenSteps
    // And I am authenticated as "trainee1" with role "TRAINEE" - defined in CommonHttpSteps

    // When I change password for "trainee1" from "old" to "new"
    // When I change password for "trainee1" from "wrong" to "new"
    @When("I change password for {string} from {string} to {string}")
    public void changePassword(String username, String oldPw, String newPw) {
        try {
            ChangePasswordDto dto = ChangePasswordDto.builder()
                    .username(username)
                    .oldPassword(oldPw)
                    .newPassword(newPw)
                    .build();
            var builder = put("/api/users/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto));

            // Вклеим Bearer из контекста
            String token = context.getAuthJwt();
            if (token != null && !token.isBlank()) {
                builder.header(HttpHeaders.AUTHORIZATION, BEARER + token);
            }

            var mvcRes = mockMvc.perform(builder).andReturn();
            context.setLastStatus(mvcRes.getResponse().getStatus());
            context.setLastBody(mvcRes.getResponse().getContentAsString());
            context.setLastError(null);
        } catch (Exception e) {
            context.setLastError(e);
        }
    }

    // Then response status should be 200 - defined in CommonHttpSteps
    // And the response body should contain "Password successfully changed" - defined in CommonHttpSteps

    
    // Given a user "trainee1" with password "old" exists - defined in CommonGivenSteps
    // And I am authenticated as "trainee1" with role "TRAINEE" - defined in CommonHttpSteps
    // When I change password for "trainee1" from "wrong" to "new" - defined above
    // Then response status should be 400 - defined in CommonHttpSteps
    // And the response body should contain "Invalid old password" - defined in CommonHttpSteps

    
    // And I am authenticated as "trainee1" with role "TRAINEE" - defined in CommonHttpSteps

    // When I logout
    @When("I logout")
    public void logout() {
        try {
            var mvcRes = mockMvc.perform(
                    post("/api/users/logout")
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
    // And the response body should contain "User logged out successfully" - defined in CommonHttpSteps

    
    // And I am not authenticated - defined in CommonHttpSteps
    // When I logout - defined above
    // Then response status should be 401 - defined in CommonHttpSteps


    private void ensureUsernameBaseFree(String base) {
        userRepository.deleteAll(userRepository.findAllByUsernameStartingWith(base));
    }
}



