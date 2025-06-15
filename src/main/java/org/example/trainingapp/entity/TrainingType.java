package org.example.trainingapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "training_types")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NonNull
    private String name;

    @Transient
    public TrainingTypeEnum getTypeEnum() {
        return TrainingTypeEnum.valueOf(name.toUpperCase());
    }

    public void setTypeEnum(TrainingTypeEnum typeEnum) {
        this.name = typeEnum.getDisplayName();
    }
}
