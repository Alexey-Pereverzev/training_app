package org.example;

import org.example.trainingapp.config.AppConfig;
import org.example.trainingapp.entity.*;
import org.example.trainingapp.facade.TrainingSystemFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("=== Registered Spring Beans ===");
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println();

        TrainingSystemFacade facade = context.getBean(TrainingSystemFacade.class);

        System.out.println("=== Loaded Trainees ===");
        facade.getTraineeService().getAllTrainees().forEach(Main::printTrainee);

        System.out.println("\n=== Loaded Trainers ===");
        facade.getTrainerService().getAllTrainers().forEach(Main::printTrainer);

        System.out.println("\n=== Loaded Trainings ===");
        facade.getTrainingService().getAllTrainings().forEach(Main::printTraining);

        System.out.println("\n=== Creating new Trainee ===");
        Trainee newTrainee = new Trainee(
                100L,
                new User("Aruzhan", "Sagatova", null, null, false),
                LocalDate.of(1998, 4, 12),
                "Gold Fitness Mangilik El"
        );
        facade.getTraineeService().createTrainee(newTrainee);
        printTrainee(facade.getTraineeService().getTrainee(100L));

        System.out.println("\n=== Creating new Trainer ===");
        Trainer newTrainer = new Trainer(
                200L,
                new User("Timur", "Baisakov", null, null, false),
                "CrossFit"
        );
        facade.getTrainerService().createTrainer(newTrainer);
        printTrainer(facade.getTrainerService().getTrainer(200L));

        System.out.println("\n=== Creating new Training ===");
        Training newTraining = new Training(
                300L,
                "CrossFit Extreme",
                new TrainingType("CrossFit"),
                LocalDate.now(),
                90,
                newTrainee,
                newTrainer
        );
        facade.getTrainingService().createTraining(newTraining);
        printTraining(facade.getTrainingService().getTraining(300L));

        System.out.println("\n=== All trainings after adding ===");
        facade.getTrainingService().getAllTrainings().forEach(Main::printTraining);

        context.close();
    }

    private static void printTrainee(Trainee t) {
        User u = t.getUser();
        System.out.printf("Trainee ID: %d | %s %s | Username: %s | Password: %s | Address: %s%n",
                t.getId(), u.getFirstName(), u.getLastName(), u.getUsername(), u.getPassword(), t.getAddress());
    }

    private static void printTrainer(Trainer t) {
        User u = t.getUser();
        System.out.printf("Trainer ID: %d | %s %s | Username: %s | Password: %s | Specialization: %s%n",
                t.getId(), u.getFirstName(), u.getLastName(), u.getUsername(), u.getPassword(), t.getSpecialization());
    }

    private static void printTraining(Training t) {
        System.out.printf("Training ID: %d | %s | Type: %s | Date: %s | Duration: %d min | Trainee: %s | Trainer: %s%n",
                t.getId(),
                t.getTrainingName(),
                t.getTrainingType().getName(),
                t.getTrainingDate(),
                t.getTrainingDuration(),
                t.getTrainee().getUser().getUsername(),
                t.getTrainer().getUser().getUsername());
    }
}