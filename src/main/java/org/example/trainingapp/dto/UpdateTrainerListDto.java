package org.example.trainingapp.dto;

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
@Schema(description = "Trainee and list of trainers to assign")
public class UpdateTrainerListDto {
    @Schema(description = "Trainee username", example = "ivan.petrov", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "List of trainer usernames to assign", example = "[\"Dina.Aliyeva\", \"Alex.Smirnov\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> trainerUsernames;
}
