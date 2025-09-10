package org.example.trainingapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Trainer hours sync result")
public record SyncResult (
        @Schema(description = "Sync transaction ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String txId,

        @Schema(description = "Trainings total count", example = "150")
        int total,

        @Schema(description = "Sent trainings count", example = "147")
        int sent,

        @Schema(description = "Spent time in millis", example = "1234")
        long tookMs) {
}
