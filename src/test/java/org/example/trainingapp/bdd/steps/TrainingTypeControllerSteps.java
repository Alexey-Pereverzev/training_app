package org.example.trainingapp.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.bdd.TestContext;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;



@RequiredArgsConstructor
public class TrainingTypeControllerSteps {

    private final TestContext context;
    private final MockMvc mockMvc;
    private final TrainingTypeRepository trainingTypeRepository;


    // Given the application test context and authentication are prepared - defined in CommonHttpSteps

    // Given training types "Yoga" and "Pilates" exist
    @Given("training types {string} and {string} exist")
    @Transactional
    public void trainingTypesExist(String t1, String t2) {
        ensureType(t1);
        ensureType(t2);
    }

    // And I am authenticated as "Elena.Sokolova" with role "TRAINER" - defined in CommonHttpSteps

    // When I request training types
    @When("I request training types")
    public void requestTrainingTypes() {
        try {
            var mvcRes = mockMvc.perform(
                    get("/api/training-types")
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
    // And the response body should contain "Yoga" - defined in CommonHttpSteps
    // And the response body should contain "Pilates" - defined in CommonHttpSteps


    // Given training types "Yoga" and "Pilates" exist - defined above
    // And I am authenticated as "Ivan.Petrov" with role "TRAINEE" - defined in CommonHttpSteps
    // When I request training types - defined above
    // Then response status should be 403 - defined in CommonHttpSteps


    // Given training types "Yoga" and "Pilates" exist - defined above
    // And I am not authenticated - defined in CommonHttpSteps
    // When I request training types - defined above
    // Then response status should be 401 - defined in CommonHttpSteps


    private void ensureType(String name) {
        trainingTypeRepository.findByName(name).orElseGet(() -> trainingTypeRepository.save(new TrainingType(name)));
    }
}

