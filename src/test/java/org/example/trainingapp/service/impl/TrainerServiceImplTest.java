package org.example.trainingapp.service.impl;

import org.example.trainingapp.converter.Converter;
import org.example.trainingapp.dao.TrainerDao;
import org.example.trainingapp.dao.TrainingTypeDao;
import org.example.trainingapp.dao.UserDao;
import org.example.trainingapp.dto.ActiveStatusDto;
import org.example.trainingapp.dto.CredentialsDto;
import org.example.trainingapp.dto.TrainerRegisterDto;
import org.example.trainingapp.dto.TrainerRequestDto;
import org.example.trainingapp.dto.TrainerResponseDto;
import org.example.trainingapp.dto.TrainingResponseDto;
import org.example.trainingapp.entity.Trainee;
import org.example.trainingapp.entity.Trainer;
import org.example.trainingapp.entity.Training;
import org.example.trainingapp.entity.TrainingType;
import org.example.trainingapp.util.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private Converter converter;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private TrainerServiceImpl trainerService;


    @Test
    void whenCreatingTrainer_shouldGenerateUsernameAndPassword() {
        // given
        TrainerRegisterDto dto = TrainerRegisterDto.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specializationName("Yoga")
                .build();

        TrainingType yoga = new TrainingType("Yoga");
        Trainer entity = Trainer.builder()
                .firstName("Dina")
                .lastName("Aliyeva")
                .specialization(yoga)
                .build();

        when(converter.dtoToEntity(dto)).thenReturn(entity);
        when(userDao.countUsersByNameAndSurname("Dina", "Aliyeva")).thenReturn(0L);
        // when
        CredentialsDto creds = trainerService.createTrainer(dto);

        // then
        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerDao).save(captor.capture());
        Trainer saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("Dina.Aliyeva");
        assertThat(saved.getPassword()).hasSize(10);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getSpecialization().getName()).isEqualTo("Yoga");
        assertThat(creds.getUsername()).isEqualTo(saved.getUsername());
        assertThat(creds.getPassword()).isEqualTo(saved.getPassword());
    }


    @Test
    void whenUpdatingTrainer_shouldUpdateEntityAndCallDao() {
        // given
        TrainingType boxing = new TrainingType("Boxing");
        Trainer existing = Trainer.builder()
                .id(10L)
                .firstName("Nina")
                .lastName("Petrova")
                .username("Nina.Petrova")
                .password("pw123")
                .specialization(boxing)
                .active(false)
                .trainees(new ArrayList<>())
                .build();

        TrainerRequestDto req = TrainerRequestDto.builder()
                .username("Nina.Petrova")
                .firstName("Nina")
                .lastName("Petrova")
                .specializationName("Boxing")
                .active(true)
                .build();

        when(trainerDao.findByUsernameWithTrainees("Nina.Petrova")).thenReturn(Optional.of(existing));
        when(trainingTypeDao.findByName("Boxing")).thenReturn(Optional.of(boxing));
        String authHeader = TestUtils.createAuthHeader("Nina.Petrova", "pw123");

        try (MockedStatic<AuthUtil> ignored = TestUtils.mockDecodeAuth("Nina.Petrova", "pw123")) {
            // when
            trainerService.updateTrainer(authHeader, req);
        }

        // then
        verify(trainerDao).update(existing);
        assertThat(existing.isActive()).isTrue();
        assertThat(existing.getSpecialization().getName()).isEqualTo("Boxing");
    }


    @Test
    void whenUpdatingTrainer_withUnsupportedSpecialization_shouldThrow() {
        // given
        Trainer existing = Trainer.builder()
                .username("Nina.Petrova")
                .password("pw")
                .build();
        when(trainerDao.findByUsernameWithTrainees("Nina.Petrova")).thenReturn(Optional.of(existing));
        TrainerRequestDto req = TrainerRequestDto.builder()
                .username("Nina.Petrova")
                .firstName("Nina")
                .lastName("Petrova")
                .specializationName("JiuJutsu")         //  absent in Enum
                .active(true)
                .build();
        String authHeader = TestUtils.createAuthHeader("Nina.Petrova", "pw");
        try (MockedStatic<AuthUtil> ignored = TestUtils.mockDecodeAuth("Nina.Petrova", "pw123")) {
            // when + then
            assertThatThrownBy(() -> trainerService.updateTrainer(authHeader, req))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("TrainingType not found: JiuJutsu");
        }
        verify(trainerDao, never()).update(any());
    }


    @Test
    void whenGettingTrainerByUsername_shouldReturnDto() {
        // given
        Trainer trainer = Trainer.builder()
                .username("Oksana.Mikhaylova")
                .trainees(new ArrayList<>())
                .build();
        TrainerResponseDto dto = TrainerResponseDto.builder()
                .username("Oksana.Mikhaylova").firstName("Oksana").build();

        when(trainerDao.findByUsernameWithTrainees("Oksana.Mikhaylova")).thenReturn(Optional.of(trainer));
        when(trainerDao.findByUsername("Oksana.Mikhaylova")).thenReturn(Optional.of(trainer));
        when(converter.entityToDtoWithoutUsername(eq(trainer), anyList())).thenReturn(dto);

        String header = TestUtils.createAuthHeader("Oksana.Mikhaylova", "pw");
        try (MockedStatic<AuthUtil> ignored = TestUtils.mockDecodeAuth("Oksana.Mikhaylova", "pw")) {
            // when
            TrainerResponseDto res = trainerService.getTrainerByUsername(header, "Oksana.Mikhaylova");
            // then
            assertThat(res.getFirstName()).isEqualTo("Oksana");
        }
    }


    @Test
    void whenSettingTrainerActiveStatus_shouldUpdate() {
        // given
        Trainer trainer = Trainer.builder()
                .id(15L).username("Serik.Nurpeisov").active(true).build();
        when(trainerDao.findByUsername("Serik.Nurpeisov")).thenReturn(Optional.of(trainer));

        ActiveStatusDto dto = new ActiveStatusDto("Serik.Nurpeisov", false);
        String header = TestUtils.createAuthHeader("Serik.Nurpeisov", "pw");
        try (MockedStatic<AuthUtil> ignored = TestUtils.mockDecodeAuth("Serik.Nurpeisov", "pw")) {
            // when
            trainerService.setTrainerActiveStatus(header, dto);
        }
        // then
        assertThat(trainer.isActive()).isFalse();
        verify(trainerDao).update(trainer);
    }


    @Test
    void whenGettingTrainerTrainings_withDateRange_shouldFilter() {
        // given
        Trainer trainer = Trainer.builder()
                .username("Serik.Nurpeisov").trainings(new ArrayList<>()).build();

        Training t1 = new Training();                       // 10-е
        t1.setTrainingDate(LocalDate.of(2024, 5, 10));
        Training t2 = new Training();                       // 20-е
        t2.setTrainingDate(LocalDate.of(2024, 5, 20));
        t2.setTrainee(new Trainee());                       // для .getUsername()
        t2.getTrainee().setUsername("Dina.Aliyeva");

        trainer.getTrainings().addAll(List.of(t1, t2));

        when(trainerDao.findByUsernameWithTrainings("Serik.Nurpeisov")).thenReturn(Optional.of(trainer));
        when(converter.entityToDtoWithNullTrainer(t2)).thenReturn(
                TrainingResponseDto.builder().date(t2.getTrainingDate()).build()
        );

        String header = TestUtils.createAuthHeader("Serik.Nurpeisov", "pw");
        try (MockedStatic<AuthUtil> ignored = TestUtils.mockDecodeAuth("Serik.Nurpeisov", "pw")) {
            // when
            var res = trainerService.getTrainerTrainings(header, "Serik.Nurpeisov",
                    LocalDate.of(2024, 5, 15), LocalDate.of(2024, 5, 25),
                    null);
            // then
            assertThat(res).hasSize(1);
            assertThat(res.getFirst().getDate()).isEqualTo(t2.getTrainingDate());
        }
    }


    @Test
    void whenGettingTrainerTrainings_withTraineeName_shouldFilter() {
        // given
        Trainer trainer = Trainer.builder().username("Serik.Nurpeisov").trainings(new ArrayList<>()).build();

        Trainee tr1 = new Trainee(); tr1.setUsername("Dina.Aliyeva");
        Trainee tr2 = new Trainee(); tr2.setUsername("Aigerim.Seilkhanova");

        Training tx1 = new Training(); tx1.setTrainee(tr1);
        Training tx2 = new Training(); tx2.setTrainee(tr2);

        trainer.getTrainings().addAll(List.of(tx1, tx2));

        when(trainerDao.findByUsernameWithTrainings("Serik.Nurpeisov")).thenReturn(Optional.of(trainer));
        when(converter.entityToDtoWithNullTrainer(tx2)).thenReturn(
                TrainingResponseDto.builder().traineeName("Aigerim.Seilkhanova").build()
        );

        String header = TestUtils.createAuthHeader("Serik.Nurpeisov", "pw");
        try (MockedStatic<AuthUtil> ignored = TestUtils.mockDecodeAuth("Serik.Nurpeisov", "pw")) {
            // when
            var res = trainerService.getTrainerTrainings(header, "Serik.Nurpeisov", null, null,
                    "Aigerim.Seilkhanova");
            // then
            assertThat(res).hasSize(1);
            assertThat(res.getFirst().getTraineeName()).isEqualTo("Aigerim.Seilkhanova");
        }
    }


    @Test
    void whenChangingTrainerPassword_shouldPersistNewPassword() {
        // given
        Trainer trainer = Trainer.builder()
                .id(99L).username("Azamat.Yeszhanov").password("oldPw").build();
        when(trainerDao.findByUsername("Azamat.Yeszhanov")).thenReturn(Optional.of(trainer));
        // when
        trainerService.setNewPassword("Azamat.Yeszhanov", "oldPw", "newPw");
        // then
        assertThat(trainer.getPassword()).isEqualTo("newPw");
        verify(trainerDao).update(trainer);
    }

}
