package org.example.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDto {
    private Long id;
    private String trainingName;
    private String trainingType;
    private LocalDate trainingDate;
    private Integer trainingDuration;
    private Long traineeId;
    private Long trainerId;
}
