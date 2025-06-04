-- Yoga
INSERT INTO users (first_name,last_name,username,password,is_active)
VALUES ('Elena','Zharkynbaeva','Elena.Zharkynbaeva','password',TRUE);
INSERT INTO trainers (id,training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Yoga'));

-- Fitness
INSERT INTO users (first_name,last_name,username,password,is_active)
VALUES ('Arman','Nurpeisov','Arman.Nurpeisov','password',TRUE);
INSERT INTO trainers (id,training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Fitness'));

-- Pilates
INSERT INTO users (first_name,last_name,username,password,is_active)
VALUES ('Oksana','Mikhaylova','Oksana.Mikhaylova','password',TRUE);
INSERT INTO trainers (id,training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Pilates'));

-- Boxing
INSERT INTO users (first_name,last_name,username,password,is_active)
VALUES ('Bekzat','Tursynov','Bekzat.Tursynov','password',TRUE);
INSERT INTO trainers (id,training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Boxing'));
