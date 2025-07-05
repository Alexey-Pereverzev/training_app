package org.example.trainingapp.service.impl;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dto.TrainingTypeDto;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private Converter converter;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    void whenGettingTrainingTypes_shouldReturnDtoList() {
        // given
        TrainingType type1 = new TrainingType();
        type1.setId(1L);
        type1.setName("Yoga");

        TrainingType type2 = new TrainingType();
        type2.setId(2L);
        type2.setName("Boxing");

        TrainingTypeDto dto1 = new TrainingTypeDto("Yoga", 1L);
        TrainingTypeDto dto2 = new TrainingTypeDto("Boxing", 2L);

        when(trainingTypeRepository.findAll()).thenReturn(List.of(type1, type2));
        when(converter.entityToDto(type1)).thenReturn(dto1);
        when(converter.entityToDto(type2)).thenReturn(dto2);

        // when
        List<TrainingTypeDto> result = trainingTypeService.getTrainingTypes();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TrainingTypeDto::getTrainingTypeName)
                .containsExactlyInAnyOrder("Yoga", "Boxing");
        assertThat(result).extracting(TrainingTypeDto::getTrainingTypeId)
                .containsExactlyInAnyOrder(1L, 2L);
    }
}
