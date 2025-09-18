package org.example.trainingapp.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Trainer extends User {
    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType specialization;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    @ManyToMany
    @JoinTable(
            name = "trainers_trainees",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private List<Trainee> trainees;


    public Trainer(Long id, String firstName, String lastName, String username, String password, boolean isActive,
                   TrainingType specialization, List<Training> trainings, List<Trainee> trainees) {
        super(id, firstName, lastName, username, password, isActive, 0, null, null);
        this.specialization = specialization;
        this.trainings = trainings;
        this.trainees = trainees;
    }
}
