package org.example.trainingapp.service;

import org.example.trainingapp.dto.TrainingTypeDto;
import java.util.List;

public interface TrainingTypeService {
    List<TrainingTypeDto> getTrainingTypes(String authHeader);
}
