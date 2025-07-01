package org.example.trainingapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating trainer information")
public class TrainerRequestDto {
    @Schema(description = "Trainer's username", example = "Alena.Ivanenko", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Trainer's first name", example = "Alena", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "Trainer's last name", example = "Ivanenko", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Trainer's specialization", example = "Pilates", requiredMode = Schema.RequiredMode.REQUIRED)
    private String specializationName;

    @Schema(description = "Trainer's active status", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean active;
}
