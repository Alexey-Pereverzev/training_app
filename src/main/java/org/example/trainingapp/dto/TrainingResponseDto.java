package org.example.trainingapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)                  //  trainee or trainer name can be null
@Schema(description = "Training session information")
public class TrainingResponseDto {
    @Schema(description = "Training name", example = "Morning Yoga")
    private String name;

    @Schema(description = "Date of the training", example = "2024-05-10")
    private LocalDate date;

    @Schema(description = "Training type", example = "Yoga")
    private String type;

    @Schema(description = "Training duration in minutes", example = "60")
    private Integer duration;

    @Schema(description = "Trainee username", example = "Ivan.Petrov")
    private String traineeName;

    @Schema(description = "Trainer username", example = "Dina.Aliyeva")
    private String trainerName;
}
