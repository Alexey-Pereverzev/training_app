package org.example.trainingapp.entity;

import lombok.Getter;

@Getter
public enum TrainingTypeEnum {
    YOGA("Yoga"),
    FITNESS("Fitness"),
    PILATES("Pilates"),
    BOXING("Boxing"),
    CROSSFIT("CrossFit");

    private final String displayName;

    TrainingTypeEnum(String displayName) {
        this.displayName = displayName;
    }
}

