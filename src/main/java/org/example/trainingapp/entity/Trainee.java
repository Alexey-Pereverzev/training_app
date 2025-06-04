package org.example.trainingapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "trainees")
@Getter
@Setter
@NoArgsConstructor
public class Trainee extends User {
    @Column(name = "birth_date", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "location", nullable = false)
    private String address;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers;

    public Trainee(Long id, String firstName, String lastName, String username, String password, boolean isActive,
                   LocalDate dateOfBirth, String address, List<Training> trainings, List<Trainer> trainers) {
        super(id, firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.trainings = trainings;
        this.trainers = trainers;
    }
}
