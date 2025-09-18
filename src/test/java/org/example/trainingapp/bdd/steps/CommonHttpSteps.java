package org.example.trainingapp.bdd.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.example.trainingapp.bdd.MockMvcAuthHelper;
import org.example.trainingapp.bdd.TestContext;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.trainingapp.constant.Constant.DEFAULT_PW;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


public class CommonHttpSteps {

    @Autowired
    private TestContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvcAuthHelper authHelper;


    // Given the application test context and authentication are prepared
    @Given("the application test context and authentication are prepared")
    public void prepareContextAndAuth() {
        context.setLastError(null);
        context.setLastString(null);
        context.setLastTrainee(null);
        context.setLastTrainer(null);
        context.setLastTraining(null);
        context.setLastTrainingRequestDto(null);
        context.setLastTrainingList(null);
        context.setLastStatus(0);
        context.setLastBody(null);
        context.setAuth(null);
        context.setAuthUser(null);
        context.setAuthRole(null);
        context.setAuthJwt(null);
    }


    // And I am authenticated as "Ivan.Petrov" with role "TRAINEE"
    // And I am authenticated as "Unknown.User" with role "TRAINEE"
    // And I am authenticated as "Not.Exist" with role "TRAINEE"
    // And I am authenticated as "Nina.Rakhimova" with role "TRAINEE"
    // And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    // And I am authenticated as "Dina.Aliyeva" with role "TRAINER"
    // And I am authenticated as "NotExist" with role "TRAINER"
    // And I am authenticated as "Unknown" with role "TRAINER"
    // And I am authenticated as "Other.Coach" with role "TRAINER"
    // And I am authenticated as "trainee1" with role "TRAINEE"
    @Given("I am authenticated as {string} with role {string}")
    public void iAmAuthenticatedAsWithRole(String username, String role) {
        context.setAuthUser(username);
        context.setAuthRole(role);
        String preferred = context.getRawPasswordOf(username);
        LinkedHashSet<String> candidates = new java.util.LinkedHashSet<>();     //  set of test passwords
        if (preferred != null && !preferred.isBlank()) candidates.add(preferred);
        candidates.add(DEFAULT_PW);
        candidates.add("password");
        candidates.add("old");
        candidates.add("new");
        String token = null;
        Integer lastStatus = null;
        String lastBody = null;
        for (String pw : candidates) {
            try {
                var dto = new CredentialsDto(username, pw);
                var res = mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                ).andReturn();
                lastStatus = res.getResponse().getStatus();
                lastBody   = res.getResponse().getContentAsString();
                if (lastStatus == 200) {
                    token = extractJwtFromBody(lastBody);
                    if (token != null && !token.isBlank()) break;
                }
            } catch (Exception ignore) {}
        }
        context.setLastStatus(lastStatus == null ? 0 : lastStatus);
        context.setLastBody(lastBody);
        context.setAuthJwt(token);
        final String bearer = (token == null || token.isBlank()) ? null : "Bearer " + token;
        RequestPostProcessor rpp = (bearer == null)
                ? anonymous()
                : req -> { req.addHeader(HttpHeaders.AUTHORIZATION, bearer); return req; };
        context.setAuth(rpp);
        assertThat(token)
                .as("JWT must be issued for user '%s' (status=%s, body=%s)", username, lastStatus, lastBody)
                .isNotBlank();
    }


    // Then response status should be 200
    // Then response status should be 404
    // Then response status should be 403
    // Then response status should be 401
    // Then response status should be 400
    // Then response status should be 201
    // Then response status should be 409
    @Then("response status should be {int}")
    public void responseStatusShouldBe(int expected) {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastStatus()).isEqualTo(expected);
    }


    // And the response body should contain "Not Found"
    // And the response body should contain "Specialization is required."
    // And the response body should contain "Trainer active status changed to true"
    // And the response body should contain "created successfully"
    // And the response body should contain "Trainer not found"
    // And the response body should contain "deleted successfully"
    // And the response body should contain "Deleting past trainings prohibited"
    // And the response body should contain "txId"
    // And the response body should contain "Yoga"
    // And the response body should contain "Pilates"
    // And the response body should contain "[]"
    // And the response body should contain "Ivan.Petrov"
    // And the response body should contain "Dina.Aliyeva"
    // And the response body should contain "Password successfully changed"
    // And the response body should contain "Invalid old password"
    // And the response body should contain "User logged out successfully"
    @Then("the response body should contain {string}")
    public void responseBodyShouldContain(String snippet) {
        assertThat(context.getLastError()).isNull();
        assertThat(context.getLastBody()).containsIgnoringCase(snippet);
    }


    // And I am not authenticated
    @Given("I am not authenticated")
    public void iAmNotAuthenticated() {
        context.setAuth(anonymous());
        context.setAuthUser(null);
        context.setAuthRole(null);
        context.setAuthJwt(null);
    }


    private String extractJwtFromBody(String body) {
        if (body == null || body.isBlank()) return null;
        try { return com.jayway.jsonpath.JsonPath.read(body, "$.token"); } catch (Exception ignore) {}
        try { return com.jayway.jsonpath.JsonPath.read(body, "$.jwt"); }   catch (Exception ignore) {}
        try { return com.jayway.jsonpath.JsonPath.read(body, "$.accessToken"); } catch (Exception ignore) {}
        return null;
    }
}