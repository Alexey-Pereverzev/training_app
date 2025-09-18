package org.example.trainingapp.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.bdd.TestContext;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TraineeRepository;
import org.example.trainingapp.repository.TrainerRepository;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.example.trainingapp.constant.Constant.DEFAULT_PW;


@RequiredArgsConstructor
public class CommonGivenSteps {

    private final TestContext context;
    private final PasswordEncoder passwordEncoder;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;


    @Before
    public void cleanContext() {
        context.setLastError(null);
        context.setLastString(null);
        context.setLastTrainee(null);
        context.setLastTrainer(null);
        context.setLastTraining(null);
        context.setLastTrainingRequestDto(null);
        context.setLastTrainingList(null);
    }


    // authentication.feature
    // Given a user "trainee1" with password "pw123" exists
    // Given a user "trainee1" with password "old" exists
    @Given("a user {string} with password {string} exists")
    public void userWithPasswordExists(String username, String rawPassword) {
        traineeRepository.findByUsername(username).ifPresentOrElse(t -> {
            t.setPassword(passwordEncoder.encode(rawPassword));
            traineeRepository.save(t);
            context.rememberRawPassword(username, rawPassword);
            context.setLastTrainee(t);
        }, () -> {
            saveUser(username, rawPassword);
            context.rememberRawPassword(username, rawPassword);
        });
    }

    // Given a user "trainer1" with role "ROLE_TRAINER" exists
    @Given("a user {string} with role {string} exists")
    public void userWithRoleExists(String username, String role) {
        String r = role == null ? "" : role.trim().toUpperCase();
        switch (r) {
            case "ROLE_TRAINER":
            case "TRAINER":
                context.setLastTrainer(ensureTrainer(username, "Boxing"));
                break;
            case "ROLE_TRAINEE":
            case "TRAINEE":
                context.setLastTrainee(ensureTrainee(username));
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }


    // trainee.feature
    // Given a trainee "Ivan.Petrov" exists
    // Given a trainee "Nina.Rakhimova" exists
    // And a trainee "Anna.Ivanova" exists
    // And a trainee "Unknown.User" exists
    @Given("a trainee {string} exists")
    public void traineeExists(String username) {
        context.setLastTrainee(ensureTrainee(username));
    }

    // And trainee "Unknown.User" is removed from DB
    @Given("trainee {string} is removed from DB")
    @Transactional
    public void traineeIsRemovedFromDb(String username) {
        traineeRepository.findByUsername(username).ifPresent(t -> {
            traineeRepository.delete(t);
            traineeRepository.flush();
        });
    }

    // And trainer "NotExist" is removed from DB
    @Given("trainer {string} is removed from DB")
    @Transactional
    public void trainerIsRemovedFromDb(String username) {
        trainerRepository.findByUsername(username).ifPresent(t -> {
            trainerRepository.delete(t);
            trainerRepository.flush();
        });
    }

    // trainer.feature and training.feature
    // Given a trainer "Dina.Aliyeva" exists
    // Given a trainer "Elena.Sokolova" exists
    @Given("a trainer {string} exists")
    public void trainerExists(String username) {
        context.setLastTrainer(ensureTrainer(username, "Yoga"));
    }


    private void saveUser(String username, String rawPassword) {
        String[] names = splitNames(username);
        switch ("ROLE_TRAINEE".toUpperCase()) {
            case "ROLE_TRAINER" -> {
                var spec = trainingTypeRepository.findByName("Boxing")
                        .orElseGet(() -> trainingTypeRepository.save(new TrainingType("Boxing")));
                Trainer trainer = Trainer.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .firstName(names[0])
                        .lastName(names[1])
                        .active(true)
                        .specialization(spec)
                        .trainings(new ArrayList<>())
                        .trainees(new ArrayList<>())
                        .build();
                trainerRepository.save(trainer);
            }
            case "ROLE_TRAINEE" -> {
                Trainee trainee = Trainee.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .firstName(names[0])
                        .lastName(names[1])
                        .active(true)
                        .dateOfBirth(LocalDate.of(1990, 1, 1)) // заглушка
                        .address("Unknown")
                        .trainings(new ArrayList<>())
                        .trainers(new ArrayList<>())
                        .build();
                traineeRepository.save(trainee);
                context.setLastTrainee(trainee);
            }
            default -> throw new IllegalArgumentException("Unknown role: " + "ROLE_TRAINEE");
        }
    }


    private TrainingType ensureType(String name) {
        return trainingTypeRepository.findByName(name)
                .orElseGet(() -> trainingTypeRepository.save(new TrainingType(name)));
    }


    private String[] splitNames(String username) {
        if (username != null && username.contains(".")) {
            String[] parts = username.split("\\.", 2);
            return new String[]{parts[0], parts[1]};
        }
        return new String[]{"User", "Unknown"};
    }


    private Trainer ensureTrainer(String username, String specName) {
        TrainingType spec = ensureType(specName);
        return trainerRepository.findByUsername(username).map(tr -> {
            tr.setPassword(passwordEncoder.encode(DEFAULT_PW));
            context.rememberRawPassword(username, DEFAULT_PW);
            if (tr.getSpecialization() == null) tr.setSpecialization(spec);
            return trainerRepository.saveAndFlush(tr);
        }).orElseGet(() -> {
            String[] names = splitNames(username);
            Trainer tr = Trainer.builder()
                    .username(username)
                    .password(passwordEncoder.encode(org.example.trainingapp.constant.Constant.DEFAULT_PW))
                    .firstName(names[0])
                    .lastName(names[1])
                    .active(true)
                    .specialization(spec)
                    .trainings(new ArrayList<>())
                    .trainees(new ArrayList<>())
                    .build();
            context.rememberRawPassword(username, org.example.trainingapp.constant.Constant.DEFAULT_PW);
            return trainerRepository.saveAndFlush(tr);
        });
    }


    private Trainee ensureTrainee(String username) {
        return traineeRepository.findByUsername(username).map(t -> {
            t.setPassword(passwordEncoder.encode(DEFAULT_PW));
            context.rememberRawPassword(username, DEFAULT_PW);
            return traineeRepository.saveAndFlush(t);
        }).orElseGet(() -> {
            String[] names = splitNames(username);
            Trainee t = Trainee.builder()
                    .username(username)
                    .password(passwordEncoder.encode(org.example.trainingapp.constant.Constant.DEFAULT_PW))
                    .firstName(names[0])
                    .lastName(names[1])
                    .active(true)
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .address("Almaty")
                    .trainings(new ArrayList<>())
                    .trainers(new ArrayList<>())
                    .build();
            context.rememberRawPassword(username, org.example.trainingapp.constant.Constant.DEFAULT_PW);
            return traineeRepository.saveAndFlush(t);
        });
    }
}
