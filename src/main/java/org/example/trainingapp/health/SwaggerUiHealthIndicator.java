package org.example.trainingapp.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class SwaggerUiHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${server.port:8080}")
    private int port;

    @Override
    public Health health() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port
                    + "/trainingapp/swagger-ui/index.html", String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("swagger", "Available").build();
            }
            return Health.down().withDetail("swagger", "Unexpected status: " + response.getStatusCode()).build();
        } catch (Exception e) {
            return Health.down().withDetail("swagger", "Exception: " + e.getMessage()).build();
        }
    }
}

