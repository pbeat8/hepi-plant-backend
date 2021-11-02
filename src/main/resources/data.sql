INSERT INTO plants.categories(id, name) VALUES
	(1, 'Brak'),
	(2, 'Rośliny ozdobne z liści'),
	(3, 'Rośliny ozdobne z kwiatów'),
	(4, 'Rośliny pnące i zwisające'),
	(5, 'Oplątwy'),
	(6, 'Sukulenty'),
	(7, 'Kaktusy') ON CONFLICT DO NOTHING;

INSERT INTO plants.species(id, fertilizing_frequency, misting_frequency, name, placement, soil, watering_frequency, category_id) VALUES
    (1, 0, 0, 'Brak', null, null, 0, null),
	(2, 14, 4, 'Aglaonema', 'JASNE', 'Bardzo kwaśna', 4, 1),
    (3, 30, 10, 'Aloes', 'JASNE', 'Uniwersalna', 7, 5),
    (4, 10, 0, 'Bonsai', 'POLCIENISTE', 'Uniwersalna', 2, 1),
    (5, 30, 7, 'Cykas', 'POLCIENISTE', 'Uniwersalna', 7, 1),
    (6, 42, 10, 'Dracena', 'JASNE', 'Lekko kwaśna', 7, 1),
    (7, 14, 5, 'Fikus', 'BARDZO_JASNE','Uniwersalna', 5, 1),
    (8, 14, 4, 'Juka', 'BARDZO_JASNE', 'Uniwersalna', 7, 1),
    (9, 14, 2, 'Kalatea', 'POLCIENISTE', 'Przepuszczalna, lekko kwaśna', 4, 1),
    (10, 16, 4, 'Maranta', 'JASNE', 'Przepuszczalna, lekko kwaśna', 5, 1),
    (11, 21, 3, 'Monstera', 'JASNE', 'Przepuszczalna', 8, 3),
    (12, 21, 4, 'Filodendron', 'POLCIENISTE', 'Żyzna, przepuszczalna', 12, 3),
    (13, 14, 4, 'Palma', 'BARDZO_JASNE', 'Uniwersalna', 5, 1),
    (14, 14, 3, 'Paproć', 'POLCIENISTE', 'Próchnicze', 7, 1),
    (15, 14, 5, 'Strelicja', 'JASNE', 'Przepuszczalna', 3, 1),
    (16, 28, 0, 'Sansewieria', 'ZACIENIONE', 'Przepuszczalna', 10, 1),
    (17, 14, 2, 'Skrzydłokwiat', 'POLCIENISTE',  'Przepuszczalna', 3, 2),
    (18, 14, 0, 'Zamiokulkas', 'ZACIENIONE', 'Uniwersalna', 10, 1),
    (19, 30, 5, 'Zroślicha','POLCIENISTE', 'Przepuszczalna', 5, 1),
    (20, 14, 3, 'Fitonia', 'ZACIENIONE', 'Przepuszczalna', 3, 1),
    (21, 14, 4, 'Pilea', 'JASNE', 'Przepuszczalna', 4, 1),
    (22, 21, 0, 'Caladium', 'JASNE', 'Przepuszczalna', 3, 1),
    (23, 14, 0, 'Alokazja', 'POLCIENISTE', 'Przepuszczalna', 7, 1) ON CONFLICT DO NOTHING;

INSERT INTO users.roles (id, name) VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER') ON CONFLICT DO NOTHING;