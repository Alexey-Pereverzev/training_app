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

| Component         | Technology                |
|-------------------|---------------------------|
| Language          | Java 21                   |
| Framework         | Spring Core 6             |
| Dependency Tool   | Gradle                    |
| Logging           | Slf4j                     |
| JPA               | Hibernate                 |
| Tests             | JUnit 5, Mockito          |
| Configuration     | YAML (`application.yaml`) |
| Storage           | PostgreSQL                |
| Migration         | Flyway                    |
| API documentation | Swagger                   |

---

## Project Structure

```
src/
├── main/
│   ├── java/org.example.trainingapp/
│   │   ├── aspect/          # AOP: authentication check, transaction logging
│   │   ├── config/          # Spring config classes
│   │   ├── controller/      # REST controllers
│   │   ├── converter/       # Entity <-> DTO conversion
│   │   ├── dao/             # DAO interfaces and implementations
│   │   ├── dto/             # DTO for service layer
│   │   ├── entity/          # Domain model: Trainee, Trainer, etc.
│   │   ├── exception/       # Custom exceptions and Controller Advice
│   │   ├── service/         # Business logic layer
│   │   └── util/            # Utilities 
│   └── resources/
│       ├── application.yml
│       ├── logback.xml
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

### 3. Copy trainingapp.war into tomcat /webapps directory

```bash
cp build/libs/trainingapp.war /path/to/tomcat/webapps/
```

### 4. Run Tomcat

```bash
/path/to/tomcat/bin/startup.bat
```

### 5. Use Postman to run requests

---

## Running Unit Tests

```bash
./gradlew test
```

Test coverage includes:
- DaoAuthenticationService
- TraineeServiceImpl
- TrainerServiceImpl
- TrainingServiceImpl
- TrainingTypeServiceImpl
- UserServiceImpl
- AuthUtil
- CredentialsUtil
- ValidationUtils

You can find test classes under:
```
src/test/java/org.example.trainingapp/
```

---

## Example Logs

```
23:05:19.003 [http-nio-8080-exec-2] INFO  o.e.t.a.TransactionLoggingAspect - [1b2ff78a-8369-4f79-98de-b2961b802484] TX-SUCCESS id=1b2ff78a-8369-4f79-98de-b2961b802484
23:05:19.027 [http-nio-8080-exec-2] INFO  o.e.t.config.RestLoggingInterceptor - [] REST-OUT POST /trainingapp/api/trainees/register -> 201
23:05:14.157 [main] WARN  org.hibernate.orm.deprecation - [] HHH90000025: PostgreSQLDialect does not need to be specified explicitly using 'hibernate.dialect' (remove the property setting and it will be selected by default)
ERROR: Critical error 
```

---

## Notes

- Credentials (username/password) are generated dynamically and stored in DB.
- Usernames are generated with duplicates prevented.
- Passwords and other sensitive data are **never** logged; only usernames, IDs, or non-confidential fields appear in logs.
- 100% test coverage of services and utils.

### Design Patterns Used
The project incorporates several established design patterns:

- **DTO** – Request and Response DTOs for service layer.
- **DAO** – abstracts in-memory data operations via `TraineeDao`, `TrainerDao`, etc.
- **Service Layer** – encapsulates business logic through `TraineeService`, `TrainerService`, etc.
- **Factory Pattern** – `YamlPropertySourceFactory` dynamically loads configuration from YAML files.
- **Builder** – Dto.builder() via Lombok.
- **Singleton** – Spring-managed beans.
- **Exception Handling (Controller Advice)** – Global error handling via ControllerAdvice

### Task implementation for module 4:

**Based on the codebase created during the previous module, implement follow REST API (as a RestController):**
1. Trainee Registration (POST method).
```
solution: TraineeController.registerTrainee(); 
```

2. Trainer Registration (POST method).
```
solution: TrainerController.registerTrainer();
```

3. Login (GET method).
```
solution: UserController.login();
```

4. Change Login (PUT method).
```
solution: UserController.changePassword();
```

5. Get Trainee Profile (GET method).
```
solution: TraineeController.getTrainee();
```

6. Update Trainee Profile (PUT method).
```
solution: TraineeController.updateTrainee();
```

7. Delete Trainee Profile (DELETE method).
```
solution: TraineeController.deleteTrainee();
```

8. Get Trainer Profile (GET method).
```
solution: TrainerController.getTrainer();
```

9. Update Trainer Profile (PUT method).
```
solution: TrainerController.updateTrainer();
```

10. Get not assigned on trainee active trainers. (GET method).
```
solution: TraineeController.getAvailableTrainers();
```

11. Update Trainee's Trainer List (PUT method).
```
solution: TraineeController.updateTrainerList();
```

12. Get Trainee Trainings List (GET method).
```
solution: TraineeController.getTraineeTrainings();
```

13. Get Trainer Trainings List (GET method).
```
solution: TrainerController.getTrainerTrainings();
```

14. Add Training (POST method).
```
solution: TrainingController.addTraining();
```

15. Activate/De-Activate Trainee (PATCH method).
```
solution: TraineeController.setActiveStatus() - with Stream API filters
```

16. Activate/De-Activate Trainer (PATCH method).
```
solution: TrainerController.setActiveStatus() - - with Stream API filters
```

17. Get Training types (GET method).
```
solution: TrainingTypeController.getTrainingTypes();
```


**Notes:**
1. During Create Trainer/Trainee profile username and password should be generated as described in previous module.
```
solution: class CredentialsUtil
```

2. Not possible to register as a trainer and trainee both.
```
solution: registering a trainee and a trainer are separate operations with unique username and password generated 
```

3. All functions except Create Trainer/Trainee profile should be executed only after Trainee/Trainer authentication (on this step should be checked username and password matching)
```
solution: annotation @RequiresAuthentication with 2 parameters: TRAINEE/TRAINER role and ownership of the method (true/false)
```

4. Implement required validation for each endpoint.
```
solution: class ValidationUtils
```

5. Users Table has parent-child (one to one) relation with Trainer and Trainee tables.
```
solution: used another approach with JOINED inheritance
```

6. Training functionality does not include delete/update possibility via REST.
```
solution: only creating and getting of Trainings are available via REST
```

7. Username cannot be changed.
```
solution: updateTrainee() / updateTrainer() functions does not allow to change username of the user
```

8. Trainees and Trainers have many to many relations.
```
solution: JoinTable trainers_trainees
```

9. Activate/De-activate Trainee/Trainer profile not idempotent action.
```
solution: changing isAcive status in DB using boolean parameter 
```

10. Delete Trainee profile is hard deleting action and bring the cascade deletion of relevant trainings.
```
solution: using cascade = CascadeType.ALL on trainings
```

11. Training duration have a number type.
```
solution: Integer trainingDuration field
```

12. Training Date, Trainee Date of Birth have Date type.
```
solution: LocalDate fields for dates
```

13. Is Active field in Trainee/Trainer profile has Boolean type.
```
solution: Boolean "active" field in User
```

14. Training Types table include constant list of values and could not be updated from the application.
```
solution: Enum TrainingTypeEnum for training type fields with validation during creation/updating of trainee/trainer 
```

15. Implement error handling for all endpoints.
```
solution: class GlobalExceptionHandler
```

16. Cover code with unit tests. 
```
solution: all services and utils are covered with unit tests.
```

17. Two levels of logging should be implemented:
a. Transaction level (generate transactionId by which you can track all operations for this transaction the same transactionId can later be passed to downstream services)
b. Specific rest call details (which endpoint was called, which request came and the service response - 200 or error and response message). 
```
solution: class TransactionLoggingAspect + class RestLoggingInterceptor
```

18. Implement error handling. 
```
solution: class GlobalExceptionHandler
```

19. Document methods in RestController file(s) using Swagger 2 annotations. 
```
solution: Swagger v3 annotations used
```

---

## Author

Aleksei Pereverzev  
Developed as part of a Java Spring Core / Hibernate learning module.


