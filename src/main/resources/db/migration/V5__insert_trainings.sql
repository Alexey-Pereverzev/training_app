
INSERT INTO trainings
  (training_name, training_type_id, training_date, training_duration,
   trainee_id, trainer_id)
VALUES
('Yoga Basics', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-01', 60,
 (SELECT id FROM users WHERE username='Alina.Iskakova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('Full Body Workout', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-02', 55,
 (SELECT id FROM users WHERE username='Marat.Nurgaliyev'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('Pilates Core', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-03', 50,
 (SELECT id FROM users WHERE username='Natalia.Petrova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('Boxing Drills', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-04', 45,
 (SELECT id FROM users WHERE username='Bagdat.Serikbay'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('Yoga Flow', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-05', 60,
 (SELECT id FROM users WHERE username='Irina.Vlasova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('Strength Training', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-06', 55,
 (SELECT id FROM users WHERE username='Asel.Tleubergenova'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('Pilates Stretch', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-07', 50,
 (SELECT id FROM users WHERE username='Azamat.Yeszhanov'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('Boxing Cardio', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-08', 45,
 (SELECT id FROM users WHERE username='Olga.Kuzmina'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('Yoga for Energy', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-09', 60,
 (SELECT id FROM users WHERE username='Aliya.Kairbekova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('HIIT Workout', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-10', 55,
 (SELECT id FROM users WHERE username='Sergey.Shapovalov'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('Pilates Balance', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-11', 50,
 (SELECT id FROM users WHERE username='Danagul.Altynbekova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('Boxing Power', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-12', 45,
 (SELECT id FROM users WHERE username='Ruslan.Zhaksylykov'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('Yoga Stretch', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-13', 60,
 (SELECT id FROM users WHERE username='Aigerim.Seilkhanova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('Fat Burn Circuit', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-14', 55,
 (SELECT id FROM users WHERE username='Yerlan.Myrzagaliyev'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('Pilates Control', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-15', 50,
 (SELECT id FROM users WHERE username='Lyudmila.Goncharova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('CrossFit Starter', (SELECT id FROM training_types WHERE name='CrossFit'),
 '2024-05-16', 60,
 (SELECT id FROM users WHERE username='Alina.Iskakova'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('CrossFit Strength', (SELECT id FROM training_types WHERE name='CrossFit'),
 '2024-05-17', 60,
 (SELECT id FROM users WHERE username='Marat.Nurgaliyev'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('Yoga Zen', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-18', 60,
 (SELECT id FROM users WHERE username='Natalia.Petrova'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('Functional Training', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-19', 55,
 (SELECT id FROM users WHERE username='Bagdat.Serikbay'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('Boxing Footwork', (SELECT id FROM training_types WHERE name='Boxing'),
 '2024-05-20', 45,
 (SELECT id FROM users WHERE username='Irina.Vlasova'),
 (SELECT id FROM users WHERE username='Bekzat.Tursynov')),

('Pilates Pulse', (SELECT id FROM training_types WHERE name='Pilates'),
 '2024-05-21', 50,
 (SELECT id FROM users WHERE username='Asel.Tleubergenova'),
 (SELECT id FROM users WHERE username='Oksana.Mikhaylova')),

('Yoga Balance', (SELECT id FROM training_types WHERE name='Yoga'),
 '2024-05-22', 60,
 (SELECT id FROM users WHERE username='Azamat.Yeszhanov'),
 (SELECT id FROM users WHERE username='Elena.Zharkynbaeva')),

('Fitness Burn', (SELECT id FROM training_types WHERE name='Fitness'),
 '2024-05-23', 55,
 (SELECT id FROM users WHERE username='Olga.Kuzmina'),
 (SELECT id FROM users WHERE username='Arman.Nurpeisov')),

('Boxing Uppercut', (SELECT id FROM training_types WHERE name='Boxing'),
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
