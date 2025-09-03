package org.example.trainingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.aspect.CheckOwnership;
import org.example.trainingapp.dto.SyncResult;
import org.example.trainingapp.dto.TrainingRequestDto;
import org.example.trainingapp.service.TrainingService;
import org.example.trainingapp.service.impl.TrainingSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final TrainingSyncService trainingSyncService;


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
        String message = trainingService.createTraining(trainingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }


    @DeleteMapping("/{trainingName}")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "Delete training", description = "Deletes a training by its name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found"),
    })
    public ResponseEntity<String> deleteTraining(
            @Parameter(description = "Name of the training", required = true)
            @PathVariable("trainingName") String trainingName) {
        trainingService.deleteTrainingByName(trainingName);
        return ResponseEntity.ok("Training " + trainingName + " deleted successfully");
    }


    @PostMapping("/sync-hours")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "Training hours sync",
            description = "Clears data in the 2nd service and resends all training info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful sync",
                    content = @Content(schema = @Schema(implementation = SyncResult.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "422", description = "Unprocessible request")
    })
    public ResponseEntity<SyncResult> syncTrainerHours() {
        SyncResult result = trainingSyncService.syncTrainerHours();
        return ResponseEntity.ok(result);
    }

}
