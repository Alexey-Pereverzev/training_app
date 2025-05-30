package org.example.trainingapp.init;

import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.User;
import org.example.trainingapp.service.TraineeService;
import org.example.trainingapp.service.TrainerService;
import org.example.trainingapp.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;


@Component
public class DataInitializer {

    private static final Logger log = Logger.getLogger(DataInitializer.class.getName());

    @Value("${trainee.data.path}")
    private Resource traineeData;

    @Value("${trainer.data.path}")
    private Resource trainerData;

    @Value("${training.data.path}")
    private Resource trainingData;

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public DataInitializer(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @PostConstruct
    public void loadData() {
        try {
            loadTrainees();
            loadTrainers();
            loadTrainings();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Critical error during data loading", e);
        }
    }

    private void loadTrainees() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(traineeData.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Long id = Long.parseLong(parts[0]);
                String first = parts[1];
                String last = parts[2];
                LocalDate dob = LocalDate.parse(parts[3]);
                String address = parts[4];
                User user = new User(first, last, null, null, false);
                Trainee t = new Trainee(id, user, dob, address);
                traineeService.createTrainee(t);
                log.info("Loaded trainee: " + first + " " + last);
            }
        }
    }

    private void loadTrainers() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(trainerData.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Long id = Long.parseLong(parts[0]);
                String first = parts[1];
                String last = parts[2];
                String specialization = parts[3];
                User user = new User(first, last, null, null, false);
                Trainer t = new Trainer(id, user, specialization);
                trainerService.createTrainer(t);
                log.info("Loaded trainer: " + first + " " + last);
            }
        }
    }


    private void loadTrainings() throws IOException {
        List<Trainee> trainees = traineeService.getAllTrainees();
        List<Trainer> trainers = trainerService.getAllTrainers();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(trainingData.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Long id = Long.parseLong(parts[0]);
                String name = parts[1];
                String type = parts[2];
                LocalDate date = LocalDate.parse(parts[3]);
                int duration = Integer.parseInt(parts[4]);
                Long traineeId = Long.parseLong(parts[5]);
                Long trainerId = Long.parseLong(parts[6]);

                Trainee trainee = trainees.stream()
                        .filter(t -> t.getId().equals(traineeId))
                        .findFirst()
                        .orElse(null);

                Trainer trainer = trainers.stream()
                        .filter(t -> t.getId().equals(trainerId))
                        .findFirst()
                        .orElse(null);

                if (trainee != null && trainer != null) {
                    Training training = new Training(
                            id,
                            name,
                            new TrainingType(type),
                            date,
                            duration,
                            trainee,
                            trainer
                    );
                    trainingService.createTraining(training);
                    log.info("Loaded training: " + name + " (" + trainee.getUser().getUsername() + " → " + trainer.getUser().getUsername() + ")");
                } else {
                    log.warning("Trainee or trainer not found for training ID=" + id);
                }
            }
        }
    }

}