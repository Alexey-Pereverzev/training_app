package org.example.trainingapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)                  //  excluding username == null for GET request
@Schema(description = "Detailed trainee profile with assigned trainers")
public class TraineeResponseDto {
    @Schema(description = "Username", example = "Ivan.Petrov")
    private String username;

    @Schema(description = "First name", example = "Ivan")
    private String firstName;

    @Schema(description = "Last name", example = "Petrov")
    private String lastName;

    @Schema(description = "Date of birth", example = "[1953,2,1]")
    private LocalDate dateOfBirth;

    @Schema(description = "Address", example = "Gold Fitness Abaya")
    private String address;

    @Schema(description = "Active status", example = "true")
    private Boolean active;

    @Schema(description = "List of assigned trainers")
    private List<TrainerShortDto> trainers;
}
