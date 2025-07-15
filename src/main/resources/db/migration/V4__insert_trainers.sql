INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Elena','Zharkynbaeva','Elena.Zharkynbaeva','$2a$10$d6Rf9Lqnp8JgN6JGFmfuwuBm9VgoSt0.DNb4zI2O.a7G7jDigjcHe',TRUE);
INSERT INTO trainers (id, training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Yoga'));

INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Arman','Nurpeisov','Arman.Nurpeisov','$2a$10$eicTa8zk5K35XxDaq8AI/eSfhZtqieRfzUxmPs7/w9jzl99Sfeat.',TRUE);
INSERT INTO trainers (id, training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Fitness'));

INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Oksana','Mikhaylova','Oksana.Mikhaylova','$2a$10$mCOW3A5HJC5gOl332sV4ceZ/Rbb3N6kD.MOah/xwTRRE.WduEHyjq',TRUE);
INSERT INTO trainers (id, training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Pilates'));

INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Bekzat','Tursynov','Bekzat.Tursynov','$2a$10$s14nndghMHPJEAUjOB/UCOrdv3tA4c/Cy5Dnw4eUN7T/m6bYqLym6',TRUE);
INSERT INTO trainers (id, training_type_id)
VALUES (currval('users_id_seq'), (SELECT id FROM training_types WHERE name='Boxing'));