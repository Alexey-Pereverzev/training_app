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
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TrainerShortDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.dto.UpdateTrainerListDto;
import org.example.trainingapp.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/trainees")
@Tag(name = "Trainees", description = "Operations related to trainees")
public class TraineeController {

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }


    @PostMapping("/register")
    @Operation(summary = "Register a new trainee", description = "Creates a new trainee and returns credentials")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Trainee created successfully",
                    content = @Content(schema = @Schema(implementation = CredentialsDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<CredentialsDto> registerTrainee(
            @Parameter(description = "Trainee registration data")
            @RequestBody TraineeRegisterDto traineeRegisterDto) {
        CredentialsDto dto = traineeService.createTrainee(traineeRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @GetMapping("/{username}")
    @Operation(summary = "Get trainee info", description = "Returns trainee profile by username")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainee found", 
                    content = @Content(schema = @Schema(implementation = TraineeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<TraineeResponseDto> getTrainee(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username) {
        TraineeResponseDto dto = traineeService.getTraineeByUsername(username);
        return ResponseEntity.ok(dto);
    }


    @PutMapping()
    @Operation(summary = "Update trainee", description = "Updates trainee profile")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainee updated",
                    content = @Content(schema = @Schema(implementation = TraineeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<TraineeResponseDto> updateTrainee(
            @Parameter(description = "Trainee update data")
            @RequestBody TraineeRequestDto traineeRequestDto) {
        TraineeResponseDto dto = traineeService.updateTrainee(traineeRequestDto);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee", description = "Deletes a trainee by username")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainee deleted successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<String> deleteTrainee(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username) {
        traineeService.deleteTrainee(username);
        return ResponseEntity.ok("Trainee deleted successfully");
    }


    @GetMapping("/{username}/available-trainers")
    @Operation(summary = "Get available trainers", description = "Returns trainers not yet assigned to trainee")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "List of available trainers",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerShortDto.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<TrainerShortDto>> getAvailableTrainers(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username) {
        List<TrainerShortDto> dtos = traineeService.getAvailableTrainersForTrainee(username);
        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/trainer-list")
    @Operation(summary = "Update trainer list", description = "Updates trainee's list of trainers")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Trainer list updated",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerShortDto.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<TrainerShortDto>> updateTrainerList(
            @Parameter(description = "Trainer list update data")
            @RequestBody UpdateTrainerListDto updateTrainerListDto) {
        List<TrainerShortDto> dtos = traineeService.updateTraineeTrainers(updateTrainerListDto);
        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{username}/trainings")
    @Operation(summary = "Get trainee trainings", description = "Filter trainee trainings by date, trainer or type")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "List of trainings",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<TrainingResponseDto>> getTraineeTrainings(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username,
            @Parameter(description = "From date (optional)")
            @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
            @Parameter(description = "To date (optional)")
            @RequestParam(name = "toDate", required = false) LocalDate toDate,
            @Parameter(description = "Trainer username (optional)")
            @RequestParam(name = "trainerName", required = false) String trainerName,
            @Parameter(description = "Training type (optional)")
            @RequestParam(name = "type", required = false) String type) {
        List<TrainingResponseDto> dtos = traineeService.getTraineeTrainings(username, fromDate, toDate,
                trainerName, type);
        return ResponseEntity.ok(dtos);
    }


    @PatchMapping("/active-status")
    @Operation(summary = "Change trainee active status", description = "Enable or disable a trainee account")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Active status changed",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<String> setActiveStatus(
            @Parameter(description = "Trainee status update data")
            @RequestBody ActiveStatusDto activeStatusDto) {
        Boolean status = traineeService.setTraineeActiveStatus(activeStatusDto);
        return ResponseEntity.ok("Trainee active status changed to " + status);
    }

}

