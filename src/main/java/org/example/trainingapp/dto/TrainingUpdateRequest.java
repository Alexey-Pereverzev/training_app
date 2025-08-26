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
@Schema(description = "Request for updating trainer's monthly hours in the trainer-hours microservice")
public class TrainingUpdateRequest {

    @Schema(description = "Username of the trainer", example = "Elena.Sokolova", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trainerUsername;

    @Schema(description = "First name of the trainer", example = "Elena")
    private String trainerFirstName;

    @Schema(description = "Last name of the trainer", example = "Sokolova")
    private String trainerLastName;

    @Schema(description = "Active status of the trainer", example = "true")
    private boolean active;

    @Schema(description = "Date of the training session", example = "2024-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate trainingDate;

    @Schema(description = "Duration of the training in minutes", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
    private int trainingDuration;

    @Schema(description = "Type of action performed: ADD or DELETE", example = "ADD", requiredMode = Schema.RequiredMode.REQUIRED)
    private ActionType actionType;
}
