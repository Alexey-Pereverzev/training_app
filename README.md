# Training Management System

This is a modular Java 21 application built using **pure Spring Core (without Spring Boot)**. It manages trainees, trainers, and training sessions in memory using Spring dependency injection, annotations, and in-memory storage.

## Features

- Manage profiles for **Trainees** and **Trainers**
- Manage **Training sessions** between them
- Load initial data from CSV files at startup
- Generate unique usernames and random passwords
- Modular architecture following **SOLID**, **KISS**, and **DRY** principles
- Main file demonstrating proper data initialization
- Structured logging at multiple levels (`INFO`, `WARNING`, `SEVERE`)
- Unit tests with **JUnit 5** and **Mockito**


---

## 🛠 Technologies Used

| Component       | Technology                   |
|-----------------|------------------------------|
| Language        | Java 21                      |
| Framework       | Spring Core 6                |
| Dependency Tool | Gradle                       |
| Logging         | java.util.logging            |
| Tests           | JUnit 5, Mockito             |
| Configuration   | YAML (`application.yaml`)    |
| Storage         | In-memory `Map<>` per entity |

---

## Project Structure

```
src/
├── main/
│   ├── java/org.example.trainingapp/
│   │   ├── config/          # Spring config classes
│   │   ├── dao/             # DAO interfaces and implementations
│   │   ├── entity/          # Domain model: Trainee, Trainer, etc.
│   │   ├── service/         # Interfaces and implementations
│   │   ├── util/            # Utilities (username/password generation)
│   │   ├── init/            # Data loader (CSV files)
│   │   └── facade/          # System facade
│   └── resources/
│       ├── application.yml
│       └── data/
│           ├── trainees.csv
│           ├── trainers.csv
│           └── trainings.csv
├── test/
│   └── java/org.example.trainingapp/
│       ├── service/         # Unit tests for service classes
│       └── util/            # Unit tests for utility classes
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

Output will include logs and entity details loaded from CSV.

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
- No external DB is used — all data is handled with Spring-managed maps.
- Usernames are generated with duplicates prevented
- Passwords and other sensitive data are **never** logged; only usernames, IDs, or non-confidential fields appear in logs.
- Although Spring’s `BeanPostProcessor` was considered, data loading is performed with `@PostConstruct`. This guarantees no risk of calling methods on partially initialized beans.
- 100% test coverage of services and utils

### Design Patterns Used
The project incorporates several established design patterns:

- **DAO** – abstracts in-memory data operations via `TraineeDao`, `TrainerDao`, and `TrainingDao`.
- **Service Layer** – encapsulates business logic through `TraineeService`, `TrainerService`, and `TrainingService`.
- **Facade Pattern** – `TrainingSystemFacade` provides a unified interface for accessing multiple service layers.
- **Factory Pattern** – `YamlPropertySourceFactory` dynamically loads configuration from YAML files.

### Property Placeholder Configuration
Spring externalises all config values through a classic **Property Placeholder** chain:

- `@PropertySource("classpath:application.yaml", factory = YamlPropertySourceFactory.class)` loads yaml properties with a custom YAML factory. 
- A `@Bean` of type `PropertySourcesPlaceholderConfigurer` activates placeholder resolution.
- Any field annotated with `@Value("${property.path}")` is injected at startup, e.g.
   ```java
   @Value("${trainee.data.path}")
   private Resource traineeCsv;      // → resources/data/trainees.csv

---

## Author

Aleksei Pereverzev
Developed as part of a Java Spring Core architecture exercise with modular design.
