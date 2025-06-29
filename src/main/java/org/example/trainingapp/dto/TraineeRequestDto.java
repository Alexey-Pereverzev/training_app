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
@Schema(description = "Data for updating a trainee")
public class TraineeRequestDto {
    @Schema(description = "Username", example = "Ivan.Petrov", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "First name", example = "Ivan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "Last name", example = "Petrov", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Active status", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean active;

    @Schema(description = "Address", example = "Gold Fitness Abaya")
    private String address;

    @Schema(description = "Date of birth", example = "1990-01-01")
    private LocalDate dateOfBirth;
}
