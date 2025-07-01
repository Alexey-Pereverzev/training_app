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
@Schema(description = "DTO for changing active status")
public class ActiveStatusDto {
    @Schema(description = "Username", example = "Ivan.Petrov", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "New active status", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean active;
}
