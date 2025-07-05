package org.example.trainingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.trainingapp.dto.TrainingTypeDto;
import org.example.trainingapp.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/training-types")
@Tag(name = "Training Types", description = "Operations related to training types")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }


    @GetMapping()
    @Operation(summary = "Get training types", description = "Returns a list of available training types for selection")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "List of training types",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingTypeDto.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes() {
        List<TrainingTypeDto> dtos = trainingTypeService.getTrainingTypes();
        return ResponseEntity.ok(dtos);
    }

}
