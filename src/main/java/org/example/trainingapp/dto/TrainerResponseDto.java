package org.example.trainingapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)                  //  excluding username == null for GET request
public class TrainerResponseDto {
    private String username;
    private String firstName;
    private String lastName;
    private String specializationName;
    private Boolean active;
    private List<TraineeShortDto> trainees;
}
