package org.example.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private boolean active;
    private String address;
    private LocalDate dateOfBirth;
    private String username;
}
