package org.example.trainingapp.health;

import org.example.trainingapp.repository.TrainingTypeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component
public class TrainingTypeCountHealthIndicator implements HealthIndicator {      //  check if DB contains Training Types

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeCountHealthIndicator(TrainingTypeRepository repository) {
        this.trainingTypeRepository = repository;
    }

    @Override
    public Health health() {
        long count = trainingTypeRepository.count();
        if (count == 0) {
            return Health.down()
                    .withDetail("error", "No training types defined")
                    .build();
        }
        return Health.up()
                .withDetail("trainingTypeCount", count)
                .build();
    }
}

