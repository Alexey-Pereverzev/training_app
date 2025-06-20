package org.example.trainingapp.controller;

import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerTrainer(@RequestBody TrainerRegisterDto trainerRegisterDto) {
        CredentialsDto dto = trainerService.createTrainer(trainerRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping()
    public ResponseEntity<?> getTrainer(@RequestParam("username") String username,
                                        @RequestHeader("password") String password) {
        TrainerResponseDto dto = trainerService.getTrainerByUsername(username, password);
        return ResponseEntity.ok(dto);
    }
}
