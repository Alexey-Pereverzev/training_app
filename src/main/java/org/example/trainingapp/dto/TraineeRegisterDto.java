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
@Schema(description = "Data needed to register a new trainee")
public class TraineeRegisterDto {
    @Schema(description = "Trainee's first name", example = "Ivan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "Trainee's last name", example = "Petrov", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Trainee's address", example = "Gold Fitness Abaya")
    private String address;

    @Schema(description = "Date of birth", example = "1990-01-01")
    private LocalDate dateOfBirth;
}
