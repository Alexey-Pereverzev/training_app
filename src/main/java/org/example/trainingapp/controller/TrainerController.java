package org.example.trainingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/trainers")
@Tag(name = "Trainers", description = "Operations related to trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }


    @Operation(summary = "Register a new trainer", description = "Creates a new trainer and returns credentials")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Trainer created successfully",
                    content = @Content(schema = @Schema(implementation = CredentialsDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/register")
    public ResponseEntity<CredentialsDto> registerTrainer(
            @Parameter(description = "Trainer registration data")
            @RequestBody TrainerRegisterDto trainerRegisterDto) {
        CredentialsDto dto = trainerService.createTrainer(trainerRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @GetMapping("/{username}")
    @Operation(summary = "Get trainer profile", description = "Returns trainer profile by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile found",
                    content = @Content(schema = @Schema(implementation = TrainerResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<TrainerResponseDto> getTrainer(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable("username") String username) {
        TrainerResponseDto dto = trainerService.getTrainerByUsername(username);
        return ResponseEntity.ok(dto);
    }


    @PutMapping()
    @Operation(summary = "Update trainer profile", description = "Updates trainer info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainerResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<TrainerResponseDto> updateTrainer(
            @Parameter(description = "Trainer profile update data")
            @RequestBody TrainerRequestDto trainerRequestDto) {
        TrainerResponseDto dto = trainerService.updateTrainer(trainerRequestDto);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainer's trainings", description = "Returns list of trainings conducted by the trainer")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "List of trainings",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<TrainingResponseDto>> getTrainerTrainings(
            @Parameter(description = "Trainer username", required = true)
            @PathVariable("username") String username,
            @Parameter(description = "Start date filter (optional)")
            @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
            @Parameter(description = "End date filter (optional)")
            @RequestParam(name = "toDate", required = false) LocalDate toDate,
            @Parameter(description = "Trainee username filter (optional)")
            @RequestParam(name = "traineeName", required = false) String traineeName) {
        List<TrainingResponseDto> dtos = trainerService.getTrainerTrainings(username, fromDate, toDate,
                traineeName);
        return ResponseEntity.ok(dtos);
    }


    @PatchMapping("/active-status")
    @Operation(summary = "Change trainer active status", description = "Sets the trainer's active status")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer status changed",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<String> setActiveStatus(
            @Parameter(description = "Trainer active status update")
            @RequestBody ActiveStatusDto activeStatusDto) {
        Boolean status = trainerService.setTrainerActiveStatus(activeStatusDto);
        return ResponseEntity.ok("Trainer active status changed to " + status);
    }

}
