# Training Management System

This is a modular Java 21 application built using **Spring Boot**. It manages trainees, trainers, and training sessions using Spring dependency injection, annotations, and PostgreSQL database.

## Features

- Manage profiles for **Trainees** and **Trainers**
- Manage **Training sessions** between them
- Initialize data using Flyway
- Generate unique usernames and random passwords
- Modular architecture following **SOLID**, **KISS**, and **DRY** principles
- REST controllers
- Swagger UI plugged in
- Structured logging at multiple levels (`INFO`, `WARNING`, `SEVERE`)
- Unit tests with **JUnit 5** and **Mockito**


---

## Technologies Used

| Component         | Technology                |
|-------------------|---------------------------|
| Language          | Java 21                   |
| Framework         | Spring Boot 3.5           |
| Dependency Tool   | Gradle                    |
| Logging           | Slf4j                     |
| JPA               | Spring Data JPA           |
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
│   │   ├── dto/             # DTO for service layer
│   │   ├── entity/          # Domain model: Trainee, Trainer, etc.
│   │   ├── exception/       # Custom exceptions and Controller Advice
│   │   ├── health/          # Custom health indicators
│   │   ├── metrics/         # Custom Prometheus metrics
│   │   ├── repository/      # Spring JPA repositories
│   │   ├── service/         # Business logic layer
│   │   └── util/            # Utilities 
│   └── resources/
│       ├── application.yaml # and profile-specific yaml-files
│       ├── logback.xml
│       └── db.migration     # Migrational scripts for Flyway
│           
└── test/
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

### 2. Run the application (default profile is 'local')

```bash
./gradlew bootRun
```

### 3. Swagger UI is available at:

```bash
http://localhost:8080/trainingapp/swagger-ui/index.html
```

### 4. Health checks exposed at:

```bash
http://localhost:8080/trainingapp/actuator/health
```

### 5. Prometheus-compatible metrics are available at:

```bash
http://localhost:8080/trainingapp/actuator/prometheus
```

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
- AuthContextUtil
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
- **Service Layer** – encapsulates business logic through `TraineeService`, `TrainerService`, etc.
- **Builder** – Dto.builder() via Lombok.
- **Singleton** – Spring-managed beans.
- **Exception Handling (Controller Advice)** – Global error handling via ControllerAdvice
- **Factory-like (Util)** - generation of username in CredentialsUtil
- **Strategy (Validation)** - Validation in class ValidationUtils
- **Decorator (Metrics)** - Timer metric
- **Adapter** - DTO <-> Entity conversion

### Task implementation for module 4:

**Based on the codebase created during the previous module, implement follow REST API (as a RestController):**
1. Convert existing application to be Spring boot Application.
```
solution: Done. Added necessary dependencies, deleted old config files and added necessary configs for Jackson parsing, Logging and Swagger. DAO layer substituted with the repository layer.
```

2. Enable actuator.
   Implement a few custom health indicators 
   Implement a few custom metrics using Prometheus 
```
solution: health indicatiors: /health package, Prometheus metrics: /metrics package
```

3. Implement support for different environments (local, dev, stg, prod). Use Spring profiles.
```
solution: implemented profiles using yaml configuration:
application-local.yaml (default)
application-dev.yaml
application-prod.yaml
application-stg.yaml
application-test.yaml
```


**Notes:**
1. Cover code with unit tests. Code should contain proper logging.
```
solution: all services and utils are covered with unit tests. All services uses SLF4J logging.
```

2. Pay attention that each environment should have different db properties.
```
solution: different db properties defined in corresponding .yaml files
```

3. All functions except Create Trainer/Trainee profile should be executed only after Trainee/Trainer authentication (on this step should be checked username and password matching)
```
solution: annotation @RequiresAuthentication with 2 parameters: TRAINEE/TRAINER role and ownership of the method (true/false). Authentication header is intercepted from the RequestContextHolder in custom AuthContextUtil 
```

---

## Author

Aleksei Pereverzev  
Developed as part of a Java Spring Core / Hibernate learning module.


