package org.example.trainingapp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
