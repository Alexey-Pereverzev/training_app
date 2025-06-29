package org.example.trainingapp.service.impl;

import org.example.trainingapp.aspect.RequiresAuthentication;
import org.example.trainingapp.aspect.Role;
import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dto.TrainingTypeDto;
import org.example.trainingapp.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private final TrainingTypeDao trainingTypeDao;
    private final Converter converter;

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeDao trainingTypeDao, Converter converter) {
        this.trainingTypeDao = trainingTypeDao;
        this.converter = converter;
    }


    @Override
    @RequiresAuthentication(allowedRoles = {Role.TRAINER}, checkOwnership = false)
    public List<TrainingTypeDto> getTrainingTypes(String authHeader) {
        return trainingTypeDao.findAll().stream().map(converter::entityToDto).toList();
    }
}
