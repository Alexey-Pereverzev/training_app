package org.example.trainingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerHoursEvent {
    private String txId;
    private EventType type;
    private TrainingUpdateRequest trainingUpdate;               // for UPDATE (ADD/DELETE)
}





