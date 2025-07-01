package org.example.trainingapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)                  //  excluding username == null for GET request
@Schema(description = "Full trainer profile data returned to the client")
public class TrainerResponseDto {
    @Schema(description = "Trainer's username", example = "Alena.Ivanenko")
    private String username;

    @Schema(description = "Trainer's first name", example = "Alena")
    private String firstName;

    @Schema(description = "Trainer's last name", example = "Ivanenko")
    private String lastName;

    @Schema(description = "Trainer's specialization", example = "Pilates")
    private String specializationName;

    @Schema(description = "Trainer's active status", example = "true")
    private Boolean active;

    @Schema(description = "List of assigned trainees")
    private List<TraineeShortDto> trainees;
}
