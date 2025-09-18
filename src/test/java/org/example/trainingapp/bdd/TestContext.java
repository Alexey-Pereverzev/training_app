package org.example.trainingapp.bdd;

import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Data
@Component
@ScenarioScope
public class TestContext {
    private Exception lastError;
    private String lastString;
    private Trainee lastTrainee;
    private Trainer lastTrainer;
    private Training lastTraining;
    private TrainingRequestDto lastTrainingRequestDto;
    private List<TrainingResponseDto> lastTrainingList;
    private int lastStatus;
    private String lastBody;

    private RequestPostProcessor auth;
    private String authUser;
    private String authRole;
    public String authJwt;

    private final Map<String, String> rawPasswordByUser = new ConcurrentHashMap<>();

    public void rememberRawPassword(String username, String rawPw) {
        if (username != null && rawPw != null) rawPasswordByUser.put(username, rawPw);
    }
    public String getRawPasswordOf(String username) {
        return username == null ? null : rawPasswordByUser.get(username);
    }
}