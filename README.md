# Training Management System

This is a modular Java 21 application built using **Spring Boot**. It manages trainees, trainers, and training sessions using Spring dependency injection, annotations, and PostgreSQL database.

## Features

- Manage profiles for **Trainees** and **Trainers**
- Manage **Training sessions** between them
- Initialize data using **Flyway**
- Generate unique usernames and random passwords
- Modular architecture following **SOLID**, **KISS**, and **DRY** principles
- **REST** controllers
- **Spring Security** configured using JWT-tokens
- **Swagger UI** plugged in
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
| Security          | Spring Security           |
| Authentication    | JWT-tokens                |
| Storage           | PostgreSQL                |
| Migration         | Flyway                    |
| API documentation | Swagger                   |

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
    └── java/org.example.trainingapp/
        ├── jwt/             # Unit tests for jwt util classes
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

### 2. Get RSA keys from the author and put them into /secret folder located in the project root

```bash
public.key
private.key
```

### 3. Run Redis on port 6379

### 4. Run the application (default profile is 'local')

```bash
./gradlew bootRun
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

---

## Running Unit Tests

```bash
./gradlew test
```

Test coverage includes:
- CustomUserDetailsService
- JpaAuthenticationService
- TraineeServiceImpl
- TrainerServiceImpl
- TrainingServiceImpl
- TrainingTypeServiceImpl
- UserServiceImpl
- AuthTokenFilter
- JwtTokenUtil
- TokenBlacklistUtil
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
- Jwt tokens are generated used RSA-encryption.
- Blacklisted tokens are stored in Redis. Redis is cleaned up of expired tokens on the application startup
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

### Task implementation for module 6:

**Based on the codebase created during the previous module, implement follow functionality:**

1. Add Spring Security module to your project and configurate it for Authentication access for all endpoints (except Create Trainer/Trainee profile).
Use Username/Password combination.
```
solution: Class SecurityConfig + @PreAuthorize. Ownership of methods are checked in AOP @CheckOwnership
```

2. Use salt and hashing to store user passwords in DB.
```
solution: BCryptPasswordEncoder
```

3. Configure Spring Security to use Login functionality.
```
solution: JwtTokenUtil.generateToken() used to return jwt-token for /login endpoint
```

4. Add Brute Force protector. Block user for 5 minutes on 3 unsuccessful logins
```
solution: added fields to User entity: LocalDateTime lockTime, LocalDateTime lastFailedLogin and boolean isAccountLocked().
Working with unsuccessful login attempts is implemented into JpaAuthenticationService.authorize() 
```

5. Implement Logout functionality and configure it in Spring Security.
```
solution: /logout andpoint + TokenBlacklistUtil class. Blacklisted tokens are checked in the security filter chain by
AuthTokenFilter.doFilterInternal()
```

6. Implement Authorization − Bearer token for Create Profile and Login functionality Use JWT token implementation.
```
solution: class JwtTokenUtil
```

7. Configure CORS policy in Spring Security.
```
solution: SecurityConfig.corsConfigurationSource() used in filterChain()
```


**Notes:**
1. During Create Trainer/Trainee profile username and password should be generated as described in previous module.
```
solution: all services and utils are covered with unit tests. All services uses SLF4J logging.
```

2. All functions except Create Trainer/Trainee profile should be executed only after Trainee/Trainer authentication (on this step should be checked username and password matching)
```
solution: @PreAuthorize + annotation @CheckOwnership  
Authentication and roles are provided from the SecurityContextHolder by custom AuthContextUtil 
```

---

## Author

Aleksei Pereverzev  
Developed as part of a Spring Security learning module.


