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
import org.example.trainingapp.dto.ChangePasswordDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.JwtResponse;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.service.AuthenticationService;
import org.example.trainingapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations related to users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;


    @PostMapping("/register-trainee")
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
        CredentialsDto dto = userService.createTrainee(traineeRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @Operation(summary = "Register a new trainer", description = "Creates a new trainer and returns credentials")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Trainer created successfully",
                    content = @Content(schema = @Schema(implementation = CredentialsDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/register-trainer")
    public ResponseEntity<CredentialsDto> registerTrainer(
            @Parameter(description = "Trainer registration data")
            @RequestBody TrainerRegisterDto trainerRegisterDto) {
        CredentialsDto dto = userService.createTrainer(trainerRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user credentials and returns user role")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "User successfully authenticated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<JwtResponse> login(
            @Parameter(description = "User credentials")
            @RequestBody CredentialsDto credentialsDto) {
        JwtResponse jwtResponse = authenticationService.authorize(credentialsDto);
        return ResponseEntity.ok(jwtResponse);
    }


    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('TRAINER', 'TRAINEE')")
    @CheckOwnership
    @Operation(summary = "Change user password", description = "Changes the password of an authenticated user")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Password successfully changed",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<String> changePassword(
            @Parameter(description = "Password change request")
            @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto);
        return ResponseEntity.ok("Password successfully changed");
    }


    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('TRAINER', 'TRAINEE')")
    @Operation(summary = "User logout", description = "Blacklist user's token")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "User logged out",
                    content = @Content(mediaType = "text/plain"))
    })
    public ResponseEntity<String> logout() {
        authenticationService.logout();
        return ResponseEntity.ok("User logged out successfully");
    }

}
