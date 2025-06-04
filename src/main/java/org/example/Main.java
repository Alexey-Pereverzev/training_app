package org.example;

import org.example.trainingapp.config.AppConfig;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.facade.TrainingSystemFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

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
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        System.out.println("=== Loaded Trainees ===");
        facade.getTraineeService().getAllTrainees().forEach(Main::printTrainee);

        System.out.println("\n=== Loaded Trainers ===");
        facade.getTrainerService().getAllTrainers().forEach(Main::printTrainer);

        System.out.println("\n=== Loaded Trainings ===");
        facade.getTrainingService().getAllTrainings().forEach(Main::printTraining);

        System.out.println("\n=== Creating new Trainee ===");
        Trainee newTrainee = createTrainee(scanner, facade, formatter);
        printTrainee(newTrainee);

        System.out.println("\n=== Creating new Trainer ===");
        Trainer newTrainer = createTrainer(scanner, facade);
        printTrainer(newTrainer);

        System.out.println("\n=== Creating new Training ===");
        Training newTraining = createTraining(scanner, facade, formatter, newTrainee, newTrainer);
        printTraining(newTraining);

        System.out.println("\n=== All trainings after adding ===");
        facade.getTrainingService().getAllTrainings().forEach(Main::printTraining);

        context.close();
    }


    private static void printTrainee(Trainee t) {
        System.out.printf("Trainee ID: %d | %s %s | Username: %s | Password: %s | Address: %s%n",
                t.getId(), t.getFirstName(), t.getLastName(), t.getUsername(), t.getPassword(), t.getAddress());
    }

    private static void printTrainer(Trainer t) {
        System.out.printf("Trainer ID: %d | %s %s | Username: %s | Password: %s | Specialization: %s%n",
                t.getId(), t.getFirstName(), t.getLastName(), t.getUsername(), t.getPassword(),
                t.getSpecialization().getName());
    }

    private static void printTraining(Training t) {
        System.out.printf("Training ID: %d | %s | Type: %s | Date: %s | Duration: %d min | Trainee: %s | Trainer: %s%n",
                t.getId(),
                t.getTrainingName(),
                t.getTrainingType().getName(),
                t.getTrainingDate(),
                t.getTrainingDuration(),
                t.getTrainee().getUsername(),
                t.getTrainer().getUsername());
    }

    private static Trainee createTrainee(Scanner scanner, TrainingSystemFacade facade, DateTimeFormatter formatter) {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        LocalDate date;
        while (true) {
            System.out.print("Enter date of birth (dd-MM-yyyy): ");
            String dateInput = scanner.nextLine();
            try {
                date = LocalDate.parse(dateInput, formatter);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please enter again (dd-MM-yyyy).");
            }
        }
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName(firstName);
        newTrainee.setLastName(lastName);
        newTrainee.setDateOfBirth(date);
        newTrainee.setAddress(address);
        facade.getTraineeService().createTrainee(newTrainee);
        return newTrainee;
    }

    private static Trainer createTrainer(Scanner scanner, TrainingSystemFacade facade) {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter specialization name: ");
        String specializationName = scanner.nextLine();
        TrainingType specialization = facade.getTrainingTypeService().getOrCreate(specializationName);
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName(firstName);
        newTrainer.setLastName(lastName);
        newTrainer.setSpecialization(specialization);
        facade.getTrainerService().createTrainer(newTrainer);
        return newTrainer;
    }

    private static Training createTraining(Scanner scanner, TrainingSystemFacade facade, DateTimeFormatter formatter,
                                           Trainee trainee, Trainer trainer) {
        System.out.print("Enter training name: ");
        String trainingName = scanner.nextLine();
        LocalDate trainingDate;
        while (true) {
            System.out.print("Enter training date (dd-MM-yyyy): ");
            String dateInput = scanner.nextLine();
            try {
                trainingDate = LocalDate.parse(dateInput, formatter);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please enter again (dd-MM-yyyy).");
            }
        }
        System.out.print("Enter training duration in minutes: ");
        int duration = Integer.parseInt(scanner.nextLine());
        Training newTraining = new Training();
        newTraining.setTrainingName(trainingName);
        newTraining.setTrainingDate(trainingDate);
        newTraining.setTrainingDuration(duration);
        newTraining.setTrainingType(trainer.getSpecialization());
        newTraining.setTrainee(trainee);
        newTraining.setTrainer(trainer);
        facade.getTrainingService().createTraining(newTraining);
        return newTraining;
    }
}