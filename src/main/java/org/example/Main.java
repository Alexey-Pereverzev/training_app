package org.example;

import org.example.trainingapp.config.AppConfig;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TrainerDto;
import org.example.trainingapp.dto.TrainingDto;
import org.example.trainingapp.facade.TrainingSystemFacade;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

    public static void oldMain(String[] args) throws InterruptedException {
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

        String testUsername = "Arman.Nurpeisov";
        String testPassword = "k6uaevsYV7";

//        System.out.println("\n=== Testing getAllTrainees with correct credentials ===");
//        listTrainees(facade, testUsername, testPassword);
//
//        System.out.println("\n=== Testing getAllTrainees with incorrect credentials ===");
//        listTrainees(facade, testUsername, "wrongPassword");

        listTrainers(facade, testUsername, testPassword);
        listTrainings(facade, testUsername, testPassword);

        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. Create new Trainee");
            System.out.println("2. Create new Trainer");
            System.out.println("3. Create new Training");
            System.out.println("4. List Trainees");
            System.out.println("5. List Trainers");
            System.out.println("6. List Trainings");
            System.out.println("7. Change Trainee Password");
            System.out.println("8. Get Trainer by ID");
            System.out.println("9. Get Trainer Trainings with Filters");
            System.out.println("0. Exit");
            Thread.sleep(100);
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
//                case "1" -> createTrainee(scanner, facade, formatter);
//                case "2" -> createTrainer(scanner, facade);
                case "3" -> createTraining(scanner, facade, formatter, testUsername, testPassword);
//                case "4" -> listTrainees(facade, testUsername, testPassword);
                case "5" -> listTrainers(facade, testUsername, testPassword);
                case "6" -> listTrainings(facade, testUsername, testPassword);
//                case "7" -> changeTraineePassword(scanner, facade);
                case "8" -> getTrainerById(scanner, facade, testUsername, testPassword);
                case "9" -> getTrainerTrainingsWithFilters(scanner, facade, testUsername, testPassword, formatter);
                case "0" -> exit = true;
                default -> System.out.println("Invalid option.");
            }
        }

        context.close();
    }



//    private static void listTrainees(TrainingSystemFacade facade, String testUsername, String testPassword) {
//        System.out.println("\n=== All Trainees ===");
//        try {
//            List<TraineeRequestDto> trainees = facade.getTraineeService().getAllTrainees(testUsername, testPassword);
//            trainees.forEach(Main::printTrainee);
//        } catch (SecurityException e) {
//            System.out.println("Authentication failed: " + e.getMessage());
//        }
//    }

    private static void listTrainers(TrainingSystemFacade facade, String testUsername, String testPassword) {
        System.out.println("\n=== All Trainers ===");
        try {
            List<TrainerDto> trainers = facade.getTrainerService().getAllTrainers(testUsername, testPassword);
            trainers.forEach(Main::printTrainer);
        } catch (SecurityException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }
    }

    private static void listTrainings(TrainingSystemFacade facade, String testUsername, String testPassword) {
        System.out.println("\n=== All Trainings ===");
        try {
            List<TrainingDto> trainings = facade.getTrainingService().getAllTrainings(testUsername, testPassword);
            trainings.forEach(Main::printTraining);
        } catch (SecurityException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }
    }


//    private static void printTrainee(TraineeRequestDto t) {
//        System.out.printf("Trainee ID: %d | %s %s | %s | Address: %s%n",
//                t.getId(), t.getFirstName(), t.getLastName(), t.getUsername(), t.getAddress());
//    }

    private static void printTrainer(TrainerDto t) {
        System.out.printf("Trainer ID: %d | %s %s | %s | Specialization: %s%n",
                t.getId(), t.getFirstName(), t.getLastName(), t.getUsername(), t.getSpecializationName());
    }

    private static void printTraining(TrainingDto t) {
        System.out.printf("Training ID: %d | %s | Type: %s | Date: %s | Duration: %d min | Trainee id: %d | Trainer id: %d%n",
                t.getId(), t.getTrainingName(), t.getTrainingType(), t.getTrainingDate(), t.getTrainingDuration(),
                t.getTraineeId(), t.getTrainerId());
    }

//    private static void createTrainee(Scanner scanner, TrainingSystemFacade facade, DateTimeFormatter formatter) {
//        System.out.println("\n=== Creating new Trainee ===");
//        System.out.print("Enter first name: ");
//        String firstName = scanner.nextLine();
//        System.out.print("Enter last name: ");
//        String lastName = scanner.nextLine();
//        LocalDate date;
//        while (true) {
//            System.out.print("Enter date of birth (dd-MM-yyyy): ");
//            String dateInput = scanner.nextLine();
//            try {
//                date = LocalDate.parse(dateInput, formatter);
//                break;
//            } catch (Exception e) {
//                System.out.println("Invalid date format. Please enter again (dd-MM-yyyy).");
//            }
//        }
//        System.out.print("Enter address: ");
//        String address = scanner.nextLine();
//        TraineeDto newTrainee = TraineeDto.builder()
//                .dateOfBirth(date)
//                .address(address)
//                .firstName(firstName)
//                .lastName(lastName)
//                .build();
//        facade.getTraineeService().createTrainee(newTrainee);
//        printTrainee(newTrainee);
//    }

//    private static void createTrainer(Scanner scanner, TrainingSystemFacade facade) {
//        System.out.println("\n=== Creating new Trainer ===");
//        System.out.print("Enter first name: ");
//        String firstName = scanner.nextLine();
//        System.out.print("Enter last name: ");
//        String lastName = scanner.nextLine();
//        TrainingType specialization = null;
//        while (specialization == null) {
//            System.out.print("Enter specialization name: ");
//            String specializationName = scanner.nextLine();
//            try {
//                specialization = facade.getConverter().getTrainingTypeByName(specializationName);
//            } catch (RuntimeException e) {
//                System.out.println("Error: " + e.getMessage() + ". Please try again.");
//            }
//        }
//        TrainerDto newTrainer = TrainerDto.builder()
//                .firstName(firstName)
//                .lastName(lastName)
//                .specializationName(specialization.getName())
//                .build();
//        facade.getTrainerService().createTrainer(newTrainer);
//        printTrainer(newTrainer);
//    }

    private static void createTraining(Scanner scanner, TrainingSystemFacade facade, DateTimeFormatter formatter,
                                       String testUsername, String testPassword) {
        System.out.println("\n=== Creating new Training ===");
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

        long traineeId;
        while (true) {
            try {
                System.out.print("Enter trainee ID: ");
                traineeId = Long.parseLong(scanner.nextLine());
                facade.getTraineeService().getTrainee(testUsername, testPassword, traineeId);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a numeric value.");
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage() + ". Please try again.");
            }
        }

        long trainerId;
        TrainerDto trainerDto;
        while (true) {
            try {
                Thread.sleep(100);
                System.out.print("Enter trainer ID: ");
                trainerId = Long.parseLong(scanner.nextLine());
                trainerDto = facade.getTrainerService().getTrainer(testUsername, testPassword, trainerId);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format. Please enter a numeric value.");
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage() + ". Please try again.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        String trainingType = trainerDto.getSpecializationName();

        TrainingDto newTraining = TrainingDto.builder()
                .trainingName(trainingName)
                .trainingDate(trainingDate)
                .trainingDuration(duration)
                .trainingType(trainingType)
                .traineeId(traineeId)
                .trainerId(trainerDto.getId())
                .build();
        facade.getTrainingService().createTraining(testUsername, testPassword, newTraining);
        printTraining(newTraining);
    }


//    private static void changeTraineePassword(Scanner scanner, TrainingSystemFacade facade) {
//        System.out.print("Enter Trainee username: ");
//        String username = scanner.nextLine();
//        System.out.print("Enter current password: ");
//        String currentPassword = scanner.nextLine();
//        System.out.print("Enter new password: ");
//        String newPassword = scanner.nextLine();
//        TraineeRequestDto traineeRequestDto = facade.getTraineeService().getTraineeByUsername(username, currentPassword);
//        Long id = traineeRequestDto.getId();
//        try {
//            facade.getTraineeService().changeTraineePassword(username, currentPassword, id, newPassword);
//            System.out.println("Password updated successfully.");
//        } catch (RuntimeException e) {
//            System.out.println("Error updating password: " + e.getMessage());
//        }
//    }


    private static void getTrainerById(Scanner scanner, TrainingSystemFacade facade, String testUsername,
                                       String testPassword) {
        System.out.print("Enter Trainer ID: ");
        long id;
        try {
            id = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a numeric value.");
            return;
        }
        try {
            TrainerDto trainer = facade.getTrainerService().getTrainer(testUsername, testPassword, id);
            printTrainer(trainer);
        } catch (RuntimeException e) {
            System.out.println("Error fetching trainer: " + e.getMessage());
        }
    }


    private static void getTrainerTrainingsWithFilters(Scanner scanner, TrainingSystemFacade facade, String testUsername,
                                                       String testPassword, DateTimeFormatter formatter) {
        System.out.print("Enter from date (dd-MM-yyyy) or leave blank: ");
        String fromDateInput = scanner.nextLine();
        LocalDate fromDate = fromDateInput.isBlank() ? null : LocalDate.parse(fromDateInput, formatter);

        System.out.print("Enter to date (dd-MM-yyyy) or leave blank: ");
        String toDateInput = scanner.nextLine();
        LocalDate toDate = toDateInput.isBlank() ? null : LocalDate.parse(toDateInput, formatter);

        System.out.print("Enter Trainee username filter or leave blank: ");
        String traineeName = scanner.nextLine();
        traineeName = traineeName.isBlank() ? null : traineeName;

        try {
            List<TrainingDto> trainings = facade.getTrainerService()
                    .getTrainerTrainings(testUsername, testPassword, fromDate, toDate, traineeName);
            if (trainings.isEmpty()) {
                System.out.println("No trainings found for the given filters.");
            } else {
                trainings.forEach(Main::printTraining);
            }
        } catch (RuntimeException e) {
            System.out.println("Error fetching trainings: " + e.getMessage());
        }
    }
}