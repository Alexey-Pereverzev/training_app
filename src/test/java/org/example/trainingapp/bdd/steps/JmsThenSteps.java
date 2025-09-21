package org.example.trainingapp.bdd.steps;

import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.service.impl.TrainerHoursPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


@RequiredArgsConstructor
public class JmsThenSteps {

    private final TrainerHoursPublisher trainerHoursPublisher;

    // And a JMS event should be published
    @Then("a JMS event should be published")
    public void jmsEventPublished() {
        verify(trainerHoursPublisher, atLeastOnce()).publishUpdate(any());
    }
}
