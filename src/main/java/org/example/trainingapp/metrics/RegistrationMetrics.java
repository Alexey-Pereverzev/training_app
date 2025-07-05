package org.example.trainingapp.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;


@Component
public class RegistrationMetrics {                      // counting the number of registrations

    private final Counter traineeRegistrationCounter;
    private final Counter trainerRegistrationCounter;

    public RegistrationMetrics(MeterRegistry registry) {
        this.traineeRegistrationCounter = registry.counter("trainee.registration.count");
        this.trainerRegistrationCounter = registry.counter("trainer.registration.count");
    }

    public void incrementTrainee() {
        traineeRegistrationCounter.increment();
    }

    public void incrementTrainer() {
        trainerRegistrationCounter.increment();
    }
}

