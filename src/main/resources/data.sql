INSERT INTO plants.categories(id, name) VALUES
	(1, 'Brak'),
	(2, 'Rośliny ozdobne z liści'),
	(3, 'Rośliny ozdobne z kwiatów'),
	(4, 'Rośliny pnące i zwisające'),
	(5, 'Oplątwy'),
	(6, 'Sukulenty'),
	(7, 'Kaktusy') ON CONFLICT DO NOTHING;

INSERT INTO plants.species(id, fertilizing_frequency, misting_frequency, name, placement, soil, watering_frequency, category_id) VALUES
    (1, 0, 0, 'Brak', null, null, 0, 1),
	(2, 14, 4, 'Aglaonema', 'JASNE', 'Bardzo kwaśna', 4, 2),
    (3, 30, 10, 'Aloes', 'JASNE', 'Uniwersalna', 7, 6),
    (4, 10, 0, 'Bonsai', 'POLCIENISTE', 'Uniwersalna', 2, 2),
    (5, 30, 7, 'Cykas', 'POLCIENISTE', 'Uniwersalna', 7, 2),
    (6, 42, 10, 'Dracena', 'JASNE', 'Lekko kwaśna', 7, 2),
    (7, 14, 5, 'Fikus', 'BARDZO_JASNE','Uniwersalna', 5, 2),
    (8, 14, 4, 'Juka', 'BARDZO_JASNE', 'Uniwersalna', 7, 2),
    (9, 14, 2, 'Kalatea', 'POLCIENISTE', 'Przepuszczalna, lekko kwaśna', 4, 2),
    (10, 16, 4, 'Maranta', 'JASNE', 'Przepuszczalna, lekko kwaśna', 5, 2),
    (11, 21, 3, 'Monstera', 'JASNE', 'Przepuszczalna', 8, 4),
    (12, 21, 4, 'Filodendron', 'POLCIENISTE', 'Żyzna, przepuszczalna', 12, 4),
    (13, 14, 4, 'Palma', 'BARDZO_JASNE', 'Uniwersalna', 5, 2),
    (14, 14, 3, 'Paproć', 'POLCIENISTE', 'Próchnicze', 7, 2),
    (15, 14, 5, 'Strelicja', 'JASNE', 'Przepuszczalna', 3, 2),
    (16, 28, 0, 'Sansewieria', 'ZACIENIONE', 'Przepuszczalna', 10, 2),
    (17, 14, 2, 'Skrzydłokwiat', 'POLCIENISTE',  'Przepuszczalna', 3, 3),
    (18, 14, 0, 'Zamiokulkas', 'ZACIENIONE', 'Uniwersalna', 10, 2),
    (19, 30, 5, 'Zroślicha','POLCIENISTE', 'Przepuszczalna', 5, 2),
    (20, 14, 3, 'Fitonia', 'ZACIENIONE', 'Przepuszczalna', 3, 2),
    (21, 14, 4, 'Pilea', 'JASNE', 'Przepuszczalna', 4, 2),
    (22, 21, 0, 'Caladium', 'JASNE', 'Przepuszczalna', 3, 2),
    (23, 14, 0, 'Alokazja', 'POLCIENISTE', 'Przepuszczalna', 7, 2) ON CONFLICT DO NOTHING;

INSERT INTO users.roles (id, name) VALUES
    (1, 'ROLE_ADMIN'),
    (2, 'ROLE_USER') ON CONFLICT DO NOTHING;