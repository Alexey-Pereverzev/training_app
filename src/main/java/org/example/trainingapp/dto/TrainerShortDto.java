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
@Schema(description = "Short trainer profile info")
public class TrainerShortDto {
    @Schema(description = "Trainer username", example = "Dina.Aliyeva")
    private String username;

    @Schema(description = "First name", example = "Dina")
    private String firstName;

    @Schema(description = "Last name", example = "Aliyeva")
    private String lastName;

    @Schema(description = "Specialization name", example = "Yoga")
    private String specializationName;
}




