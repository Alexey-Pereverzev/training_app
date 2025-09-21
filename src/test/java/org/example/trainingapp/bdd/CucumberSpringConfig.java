package org.example.trainingapp.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.example.trainingapp.service.impl.TrainerHoursPublisher;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
public class CucumberSpringConfig {

    @MockitoBean
    TrainerHoursPublisher trainerHoursPublisher;    // Mock of broker for all services
}
