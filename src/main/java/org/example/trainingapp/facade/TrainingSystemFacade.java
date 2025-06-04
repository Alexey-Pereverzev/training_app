package org.example.trainingapp.facade;

import lombok.Getter;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.service.TrainingService;
import org.example.trainingapp.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Getter
@Component
public class TrainingSystemFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingSystemFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService,
            TrainingTypeService trainingTypeService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }
}
