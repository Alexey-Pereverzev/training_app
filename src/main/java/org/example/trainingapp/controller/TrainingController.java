package org.example.trainingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/trainings")
@Tag(name = "Trainings", description = "Operations related to trainings")
public class TrainingController {
    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }


    @PostMapping()
    @Operation(summary = "Create a new training", description = "Creates a training session and returns confirmation message")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<?> addTraining(
            @Parameter(description = "Training details")
            @RequestBody TrainingRequestDto trainingRequestDto,
            @Parameter(description = "Authorization header (Basic Auth)", required = true)
            @RequestHeader("Authorization") String authHeader) {
        String name = trainingService.createTraining(authHeader, trainingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Training " + name + " created successfully");
    }

}
