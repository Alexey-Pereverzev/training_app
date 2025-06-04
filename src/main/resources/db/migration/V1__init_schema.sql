CREATE TABLE training_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL
);

CREATE TABLE trainees (
    id BIGINT PRIMARY KEY REFERENCES users(id),
    birth_date DATE NOT NULL,
    location VARCHAR(255) NOT NULL
);

CREATE TABLE trainers (
    id BIGINT PRIMARY KEY REFERENCES users(id),
    training_type_id BIGINT NOT NULL REFERENCES training_types(id)
);

CREATE TABLE trainings (
    id BIGSERIAL PRIMARY KEY,
    training_name VARCHAR(255) NOT NULL,
    training_type_id BIGINT NOT NULL REFERENCES training_types(id),
    training_date DATE NOT NULL,
    training_duration INT NOT NULL,
    trainee_id BIGINT NOT NULL REFERENCES trainees(id),
    trainer_id BIGINT NOT NULL REFERENCES trainers(id)
);

CREATE TABLE trainers_trainees (
    trainer_id BIGINT NOT NULL REFERENCES trainers(id),
    trainee_id BIGINT NOT NULL REFERENCES trainees(id),
    PRIMARY KEY (trainer_id, trainee_id)
);