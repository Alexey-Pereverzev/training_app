package org.example.trainingapp.service;

import org.example.trainingapp.entity.TrainingType;

public interface TrainingTypeService {
    TrainingType getOrCreate(String name);
}
