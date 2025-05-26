package org.example.trainingapp.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainee {
    private Long id;
    private User user;
    private LocalDate dateOfBirth;
    private String address;
}
