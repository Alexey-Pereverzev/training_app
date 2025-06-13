# Training Management System

This is a modular Java 21 application built using **pure Spring Core (without Spring Boot) and Hibernate**. It manages trainees, trainers, and training sessions in memory using Spring dependency injection, annotations, and in-memory storage.

## Features

- Manage profiles for **Trainees** and **Trainers**
- Manage **Training sessions** between them
- Initialize data using Flyway
- Generate unique usernames and random passwords
- Modular architecture following **SOLID**, **KISS**, and **DRY** principles
- Main file demonstrating data initialization and different functions
- Structured logging at multiple levels (`INFO`, `WARNING`, `SEVERE`)
- Unit tests with **JUnit 5** and **Mockito**


---

## Technologies Used

| Component       | Technology                |
|-----------------|---------------------------|
| Language        | Java 21                   |
| Framework       | Spring Core 6             |
| Dependency Tool | Gradle                    |
| Logging         | java.util.logging         |
| JPA             | Hibernate                 |
| Tests           | JUnit 5, Mockito          |
| Configuration   | YAML (`application.yaml`) |
| Storage         | PostgreSQL                |
| Migration       | Flyway                    |

---

## Project Structure

```
src/
├── main/
│   ├── java/org.example.trainingapp/
│   │   ├── aspect/          # AOP authentication check
│   │   ├── config/          # Spring config classes
│   │   ├── converter/       # Entity <-> DTO conversion
│   │   ├── dao/             # DAO interfaces and implementations
│   │   ├── dto/             # DTO for service layer
│   │   ├── entity/          # Domain model: Trainee, Trainer, etc.
│   │   ├── facade/          # System facade
│   │   ├── service/         # Interfaces and implementations
│   │   └── util/            # Utilities (username/password generation)
│   └── resources/
│       ├── application.yml
│       └── db.migration     # Migrational scripts for Flyway
│           
├── test/
    └── java/org.example.trainingapp/
        ├── service/         # Unit tests for service classes
        └── util/            # Unit tests for utility classes
```

---

## How to Build and Run

### 1. Clone the repository

```bash
git clone https://github.com/Alexey-Pereverzev/training_app.git
cd training_app
```

### 2. Build the project

```bash
./gradlew clean build
```

### 3. Run the application

```bash
./gradlew run
```

Output will include logs and entity details loaded from DB.

---

## Running Unit Tests

```bash
./gradlew test
```

Test coverage includes:
- TraineeServiceImpl
- TrainerServiceImpl
- TrainingServiceImpl
- CredentialsUtil
- ValidationUtils
- DaoAuthenticationService

You can find test classes under:
```
src/test/java/org.example.trainingapp/
```

---

## Example Logs

```
INFO: Created trainee: Alina.Iskakova
WARNING: Trainer not found: ID=123
SEVERE: Critical error during data loading
```

---

## Notes

- Credentials (username/password) are generated dynamically and stored in-memory.
- Usernames are generated with duplicates prevented.
- Passwords and other sensitive data are **never** logged; only usernames, IDs, or non-confidential fields appear in logs.
- 100% test coverage of services and utils.

### Design Patterns Used
The project incorporates several established design patterns:

- **DTO** – TraineeDto, TrainerDto, TrainingDto for service layer.
- **DAO** – abstracts in-memory data operations via `TraineeDao`, `TrainerDao`, and `TrainingDao`.
- **Service Layer** – encapsulates business logic through `TraineeService`, `TrainerService`, and `TrainingService`.
- **Facade Pattern** – `TrainingSystemFacade` provides a unified interface for accessing multiple service layers.
- **Factory Pattern** – `YamlPropertySourceFactory` dynamically loads configuration from YAML files.
- **Builder** – Dto.builder() via Lombok.
- **Singleton** – Spring-managed beans.

### Task implementation for module 3:

**On the codebase created during the previous module implement follow functionality:**
1. Create Trainer profile.
```
solution: TrainerServiceImpl.createTrainer(); 
```

2. Create Trainee profile.
```
solution: TraineeServiceImpl.createTrainee();
```

3. Trainee username and password matching.
4. Trainer username and password matching.
```
solution: class DaoAuthenticationService
```

5. Select Trainer profile by username.
```
solution: TrainerServiceImpl.getTrainerByUsername();
```

6. Select Trainee profile by username.
```
solution: TraineeServiceImpl.getTraineeByUsername();
```

7. Trainee password change.
```
solution: TraineeServiceImpl.changeTraineePassword();
```

8. Trainer password change.
```
solution: TrainerServiceImpl.changeTrainerPassword();
```

9. Update trainer profile.
```
solution: TrainerServiceImpl.updateTrainer();
```

10. Update trainee profile.
```
solution: TraineeServiceImpl.updateTrainee();
```

11. Activate/De-activate trainee.
```
solution: TraineeServiceImpl.setTraineeActiveStatus();
```

12. Activate/De-activate trainer.
```
solution: TrainerServiceImpl.setTrainerActiveStatus();
```

13. Delete trainee profile by username.
```
solution: TraineeServiceImpl.deleteTraineeByUsername();
```

14. Get Trainee Trainings List by trainee username and criteria (from date, to date, trainer name, training type).
```
solution: TraineeServiceImpl.getTraineeTrainings() - with Stream API filters
```

15. Get Trainer Trainings List by trainer username and criteria (from date, to date, trainee name).
```
solution: TrainerServiceImpl.getTrainerTrainings() - with Stream API filters
```

16. Add training.
```
solution: TrainingServiceImpl.createTraining();
```

17. Get trainers list that not assigned on trainee by trainee's username.
```
solution: TraineeServiceImpl.getAvailableTrainersForTrainee();
```

18. Update Trainee's trainers list
```
solution: TraineeServiceImpl.updateTraineeTrainers();
```

**Notes:**
1. During Create Trainer/Trainee profile username and password should be generated as described in previous module.
```
solution: class CredentialsUtil
```

2. All functions except Create Trainer/Trainee profile should be executed only after Trainee/Trainer authentication (on this step should be checked username and password matching)
```
solution: annotation @RequiresAuthentication with 2 parameters: TRAINEE/TRAINER role and ownership of the method (true/false)
```

3. Pay attention on required field validation before Create/Update action execution.
```
solution: class ValidationUtils
```

4. Users Table has parent-child (one to one) relation with Trainer and Trainee tables.
```
solution: used another approach with JOINED inheritance
```

5. Trainees and Trainers have many to many relations.
```
solution: JoinTable trainers_trainees
```

6. Activate/De-activate Trainee/Trainer profile not idempotent action.
```
solution: changing isAcive status in DB using boolean parameter 
```

7. Delete Trainee profile is hard deleting action and bring the cascade deletion of relevant trainings.
```
solution: using cascade = CascadeType.ALL on trainings
```

8. Training duration have a number type.
```
solution: Integer trainingDuration field
```

9. Training Date, Trainee Date of Birth have Date type.
```
solution: LocalDate fields for dates
```

10. Training related to Trainee and Trainer by FK.
```
solution: @ManyToOne relationship via trainee/trainer id
```

11. Is Active field in Trainee/Trainer profile has Boolean type.
```
solution: boolean "active" field in User
```

12. Training Types table include constant list of values and could not be updated from the application.
```
solution: Enum TrainingTypeEnum for training type fields with validation during creation/updating of trainee/trainer 
```

13. Each table has its own PK.
```
solution: id fields with @Id annotation
```

14. Try to imagine what are the reason behind the decision to save Training and Training Type tables separately with one-to-many relation.
```
answer: we have benefits of data normalization, including reducing of duplication, code readability and simplification
```

15. Use transaction management to perform actions in a transaction where it necessary.
```
solution: manual EntityTransaction management in DAO layer.
```

16. Configure Hibernate for work with DBMS that you choose.
```
solution: AppConfig.hibernateProperties(), transactionManager()
```

17. Cover code with unit tests. Code should contain proper logging.
```
solution: all services and utils are covered with unit tests.
```

---

## Author

Aleksei Pereverzev  
Developed as part of a Java Spring Core / Hibernate learning module.


