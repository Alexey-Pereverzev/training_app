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
@Schema(description = "Short trainee profile info")
public class TraineeShortDto {
    @Schema(description = "Username", example = "Ivan.Petrov")
    private String username;

    @Schema(description = "First name", example = "Ivan")
    private String firstName;

    @Schema(description = "Last name", example = "Petrov")
    private String lastName;
}
