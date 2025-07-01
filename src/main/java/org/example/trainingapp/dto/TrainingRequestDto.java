package org.example.trainingapp.dto;

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
@Schema(description = "Request body for creating a new training")
public class TrainingRequestDto {
    @Schema(description = "Name of the training", example = "Power Yoga", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Date of the training", example = "2024-05-10", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "Duration of the training in minutes", example = "60", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer duration;

    @Schema(description = "Username of the trainee", example = "Anna.Ivanova", requiredMode = Schema.RequiredMode.REQUIRED)
    private String traineeName;

    @Schema(description = "Username of the trainer", example = "Elena.Sokolova", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainerName;
}
