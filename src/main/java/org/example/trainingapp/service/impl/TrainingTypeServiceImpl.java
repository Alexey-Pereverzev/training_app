package org.example.trainingapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.TrainingTypeDto;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.example.trainingapp.service.TrainingTypeService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final Converter converter;


    @Override
    public List<TrainingTypeDto> getTrainingTypes() {
        return trainingTypeRepository.findAll().stream().map(converter::entityToDto).toList();
    }
}
