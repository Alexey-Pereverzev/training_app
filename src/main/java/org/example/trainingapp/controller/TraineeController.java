package org.example.trainingapp.controller;

import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TraineeRequestDto;
import org.example.trainingapp.dto.TraineeRegisterDto;
import org.example.trainingapp.dto.TraineeResponseDto;
import org.example.trainingapp.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerTrainee(@RequestBody TraineeRegisterDto traineeRegisterDto) {
        CredentialsDto dto = traineeService.createTrainee(traineeRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping()
    public ResponseEntity<?> getTrainee(@RequestParam("username") String username,
                                        @RequestHeader("password") String password) {
        TraineeResponseDto dto = traineeService.getTraineeByUsername(username, password);
        return ResponseEntity.ok(dto);
    }

    @PutMapping()
    public ResponseEntity<?> updateTrainee(@RequestBody TraineeRequestDto traineeRequestDto,
                                              @RequestHeader("username") String username,
                                              @RequestHeader("password") String password) {
        TraineeResponseDto dto = traineeService.updateTrainee(username, password, traineeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteTrainee(@RequestParam("username") String username,
                                           @RequestHeader("password") String password) {
        traineeService.deleteTrainee(username, password);
        return ResponseEntity.ok().build();
    }





    @GetMapping
    public ResponseEntity<List<TraineeRequestDto>> getAllTrainees(@RequestHeader("username") String username,
                                                                  @RequestHeader("password") String password) {
        return ResponseEntity.ok(traineeService.getAllTrainees(username, password));
    }
}

