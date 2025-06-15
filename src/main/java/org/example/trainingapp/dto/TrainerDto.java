package org.example.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto {
    private Long id;
    private String firstName;
    private String lastName;
    private boolean active;
    private String specializationName;
    private String username;
}
