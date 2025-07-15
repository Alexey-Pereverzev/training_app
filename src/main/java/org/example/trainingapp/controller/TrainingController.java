package org.example.trainingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.aspect.CheckOwnership;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/trainings")
@Tag(name = "Trainings", description = "Operations related to trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;


    @PostMapping()
    @PreAuthorize("hasRole('TRAINER')")
    @CheckOwnership
    @Operation(summary = "Create a new training", description = "Creates a training session and returns confirmation message")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Training created successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<String> addTraining(
            @Parameter(description = "Training details")
            @RequestBody TrainingRequestDto trainingRequestDto) {
        String name = trainingService.createTraining(trainingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Training " + name + " created successfully");
    }

}
