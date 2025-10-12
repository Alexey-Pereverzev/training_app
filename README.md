# Training Management System

This is a modular Java 21 / Spring Boot 3.5 application built using **Spring Boot**. It manages trainees, trainers, and training sessions using Spring dependency injection, annotations, PostgreSQL database, Redis, JWT, ActiveMQ, Flyway, Prometheus and Swagger.

## Features

- Manage profiles for **Trainees** and **Trainers**
- Manage **Training sessions** between them
- JWT authentication + filters/logging aspects
- Initialize data using **Flyway**
- Generate unique usernames and random passwords
- Redis for token blacklist
- Modular architecture following **SOLID**, **KISS**, and **DRY** principles
- **REST** controllers
- **Spring Security** configured using JWT-tokens
- **Swagger UI** plugged in
- Structured logging at multiple levels (`INFO`, `WARNING`, `SEVERE`)
- Unit tests with **JUnit 5** and **Mockito**
- ActiveMQ for microservice communication



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
| Security          | Spring Security           |
| Authentication    | JWT-tokens                |
| Storage           | PostgreSQL                |
| Migration         | Flyway                    |
| Key-value storage | Redis                     |
| API documentation | Swagger                   |
| Messaging         | ActiveMQ                  |
| Integration tests | Cucumber                  |
| Containerization  | Docker                    |

---

## Project Structure

```
src/
├── main/
│   ├── java/org.example.trainingapp/
│   │   ├── aspect/          # AOP: authentication method ownership, transaction logging
│   │   ├── config/          # Spring config classes
│   │   ├── constant/        # String constants
│   │   ├── controller/      # REST controllers
│   │   ├── converter/       # Entity <-> DTO conversion
│   │   ├── dto/             # DTO for service layer
│   │   ├── entity/          # Domain model: Trainee, Trainer, etc.
│   │   ├── exception/       # Custom exceptions and Controller Advice
│   │   ├── filter/          # Security and logging filters
│   │   ├── health/          # Custom health indicators
│   │   ├── jwt/             # JWT utils
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
    ├── java/org.example.trainingapp/
    │   ├── bdd/             # Cucumber files - steps and config
    │   ├── config/          # Unit tests for JSON converter
    │   ├── filter/          # Unit tests for filter classes
    │   ├── jwt/             # Unit tests for jwt util classes
    │   ├── service/         # Unit tests for service classes
    │   └── util/            # Unit tests for utility classes
    └── resources/
        └── features/        # Cucumber features
```

---

## How to Build and Run

### 1. Clone the repository

```bash
git clone https://github.com/Alexey-Pereverzev/training_app.git
cd training_app
```

### 2. Get RSA keys from the author and put them into /secret folder located in the project root

```bash
public.key
private.key
```

### 3. Tests (JUnit + Cucumber):

```bash
./gradlew test
```

### 4. Run services:

```bash
docker compose up -d --build
```

### 5. Swagger UI is available at:

```bash
http://localhost:8080/trainingapp/swagger-ui/index.html
```

### 6. Health checks exposed at:

```bash
http://localhost:8080/trainingapp/actuator/health
```

### 7. Prometheus-compatible metrics are available at:

```bash
http://localhost:8080/trainingapp/actuator/prometheus
```

### 8. Logs:

```bash
docker compose logs -f trainingapp  # trainingapp
docker compose logs -f activemq     # ActiveMq
docker compose logs -f pg           # PostgreSQL
docker compose logs -f redis        # Redis
docker compose logs -f mongo        # Mongo
```

### 9. Stop services:

```bash
docker compose down
```

---

## Unit tests:

Test coverage includes:
- FixedTypeJsonMessageConverter
- AuthTokenFilter
- RestLoggingFilter
- TransactionIdFilter
- JwtTokenUtil
- TokenBlacklistUtil
- CustomUserDetailsService
- JpaAuthenticationService
- TraineeServiceImpl
- TrainerHoursClientImpl
- TrainerServiceImpl
- TrainingInitializationService
- TrainingServiceImpl
- TrainingTypeServiceImpl
- UserServiceImpl
- AuthContextUtil
- CredentialsUtil
- ValidationUtils

You can find test classes under:
```
src/test/java/org.example.trainingapp/
```

## Integration tests:

Test coverage includes:
- Trainee controller
- Trainer controller
- Training controller
- Training Type controller
- User controller


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
- Jwt tokens are generated used RSA-encryption.
- Blacklisted tokens are stored in Redis. Redis is cleaned up of expired tokens on the application startup (RedisBlacklistCleanup).
- Passwords and other sensitive data are **never** logged; only usernames, IDs, or non-confidential fields appear in logs.
- 100% test coverage of services and utils.
- For testing purposes 15 trainees and 4 trainers with hashed passwords added. Original passwords are their names in lower case, for example: username "Oksana.Mikhaylova", password: "oksana".
- For calculating training hours separate training-hours-service used. It's getting requests using RestTemplate
- After flyway migration Mongo db in 2nd microservice is overwritten with current training hours

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

---

### Task implementation for module 10:

**Tasks:**

1. Create Dockerfile for Main microservice with disabled integrations. Create Docker images from the files. Run application.
```
solution: Dockerfile. Can run with docker run command.
```

2. Setup network configuration for Docker. Run application with enabled integrations with DB/queue.
```
solution: docker-compose.yml. Run: docker compose up -d --build
```

3. Start a shell in the running Docker containers and check the application logs.
```
solution: docker compose logs -f trainingapp
```

---

## Author

Aleksei Pereverzev  
Developed as part of a Docker learning module.


