
INSERT INTO trainings
  (training_name, training_type_id, training_date, training_duration,
   trainee_id, trainer_id)
VALUES
('2024-05-01#1 - Yoga Basics', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-01', 60,
 (SELECT id FROM users WHERE username='Alina.Iskakova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('2024-05-02#1 - Full Body Workout', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-02', 55,
 (SELECT id FROM users WHERE username='Marat.Nurgaliyev'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('2024-05-03#1 - Pilates Core', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-03', 50,
 (SELECT id FROM users WHERE username='Natalia.Petrova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('2024-05-04#1 - Boxing Drills', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-04', 45,
 (SELECT id FROM users WHERE username='Bagdat.Serikbay'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('2024-05-05#1 - Yoga Flow', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-05', 60,
 (SELECT id FROM users WHERE username='Irina.Vlasova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('2024-05-06#1 - Strength Training', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-06', 55,
 (SELECT id FROM users WHERE username='Asel.Tleubergenova'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('2024-05-07#1 - Pilates Stretch', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-07', 50,
 (SELECT id FROM users WHERE username='Azamat.Yeszhanov'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('2024-05-08#1 - Boxing Cardio', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-08', 45,
 (SELECT id FROM users WHERE username='Olga.Kuzmina'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('2024-05-09#1 - Yoga for Energy', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-09', 60,
 (SELECT id FROM users WHERE username='Aliya.Kairbekova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('2024-05-10#1 - HIIT Workout', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-10', 55,
 (SELECT id FROM users WHERE username='Sergey.Shapovalov'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('2024-05-11#1 - Pilates Balance', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-11', 50,
 (SELECT id FROM users WHERE username='Danagul.Altynbekova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('2024-05-12#1 - Boxing Power', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-12', 45,
 (SELECT id FROM users WHERE username='Ruslan.Zhaksylykov'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('2024-05-13#1 - Yoga Stretch', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-13', 60,
 (SELECT id FROM users WHERE username='Aigerim.Seilkhanova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('2024-05-14#1 - Fat Burn Circuit', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-14', 55,
 (SELECT id FROM users WHERE username='Yerlan.Myrzagaliyev'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('2024-05-15#1 - Pilates Control', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-15', 50,
 (SELECT id FROM users WHERE username='Lyudmila.Goncharova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('2024-05-16#1 - CrossFit Starter', (SELECT id FROM training_types WHERE name='CrossFit'),
 '2024-05-16', 60,
 (SELECT id FROM users WHERE username='Alina.Iskakova'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('2024-05-17#1 - CrossFit Strength', (SELECT id FROM training_types WHERE name='CrossFit'),
 '2024-05-17', 60,
 (SELECT id FROM users WHERE username='Marat.Nurgaliyev'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('2024-05-18#1 - Yoga Zen', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-18', 60,
 (SELECT id FROM users WHERE username='Natalia.Petrova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('2024-05-19#1 - Functional Training', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-19', 55,
 (SELECT id FROM users WHERE username='Bagdat.Serikbay'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('2024-05-20#1 - Boxing Footwork', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-20', 45,
 (SELECT id FROM users WHERE username='Irina.Vlasova'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('2024-05-21#1 - Pilates Pulse', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-21', 50,
 (SELECT id FROM users WHERE username='Asel.Tleubergenova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('2024-05-22#1 - Yoga Balance', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-22', 60,
 (SELECT id FROM users WHERE username='Azamat.Yeszhanov'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('2024-05-23#1 - Fitness Burn', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-23', 55,
 (SELECT id FROM users WHERE username='Olga.Kuzmina'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('2024-05-24#1 - Boxing Uppercut', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-24', 45,
 (SELECT id FROM users WHERE username='Aliya.Kairbekova'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov'));



INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Elena.Zharkynbaeva'),
       (SELECT id FROM users WHERE username = 'Alina.Iskakova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Arman.Nurpeisov'),
       (SELECT id FROM users WHERE username = 'Marat.Nurgaliyev')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Oksana.Mikhaylova'),
       (SELECT id FROM users WHERE username = 'Natalia.Petrova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Bekzat.Tursynov'),
       (SELECT id FROM users WHERE username = 'Bagdat.Serikbay')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Elena.Zharkynbaeva'),
       (SELECT id FROM users WHERE username = 'Irina.Vlasova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Arman.Nurpeisov'),
       (SELECT id FROM users WHERE username = 'Asel.Tleubergenova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Oksana.Mikhaylova'),
       (SELECT id FROM users WHERE username = 'Azamat.Yeszhanov')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Bekzat.Tursynov'),
       (SELECT id FROM users WHERE username = 'Olga.Kuzmina')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Elena.Zharkynbaeva'),
       (SELECT id FROM users WHERE username = 'Aliya.Kairbekova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Arman.Nurpeisov'),
       (SELECT id FROM users WHERE username = 'Sergey.Shapovalov')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Oksana.Mikhaylova'),
       (SELECT id FROM users WHERE username = 'Danagul.Altynbekova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Bekzat.Tursynov'),
       (SELECT id FROM users WHERE username = 'Ruslan.Zhaksylykov')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Elena.Zharkynbaeva'),
       (SELECT id FROM users WHERE username = 'Aigerim.Seilkhanova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Arman.Nurpeisov'),
       (SELECT id FROM users WHERE username = 'Yerlan.Myrzagaliyev')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Oksana.Mikhaylova'),
       (SELECT id FROM users WHERE username = 'Lyudmila.Goncharova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Bekzat.Tursynov'),
       (SELECT id FROM users WHERE username = 'Alina.Iskakova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Bekzat.Tursynov'),
       (SELECT id FROM users WHERE username = 'Marat.Nurgaliyev')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Elena.Zharkynbaeva'),
       (SELECT id FROM users WHERE username = 'Natalia.Petrova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Arman.Nurpeisov'),
       (SELECT id FROM users WHERE username = 'Bagdat.Serikbay')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Bekzat.Tursynov'),
       (SELECT id FROM users WHERE username = 'Irina.Vlasova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Oksana.Mikhaylova'),
       (SELECT id FROM users WHERE username = 'Asel.Tleubergenova')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Elena.Zharkynbaeva'),
       (SELECT id FROM users WHERE username = 'Azamat.Yeszhanov')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Arman.Nurpeisov'),
       (SELECT id FROM users WHERE username = 'Olga.Kuzmina')
ON CONFLICT DO NOTHING;

INSERT INTO trainers_trainees (trainer_id, trainee_id)
SELECT (SELECT id FROM users WHERE username = 'Bekzat.Tursynov'),
       (SELECT id FROM users WHERE username = 'Aliya.Kairbekova')
ON CONFLICT DO NOTHING;
