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
@Schema(description = "Training type info")
public class TrainingTypeDto {
    @Schema(description = "Training type name", example = "Yoga")
    private String trainingTypeName;

    @Schema(description = "Training type id", example = "1")
    private Long trainingTypeId;
}
