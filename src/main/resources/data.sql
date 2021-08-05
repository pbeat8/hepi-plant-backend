INSERT INTO plants.categories(id, name) VALUES
	(101, 'Rośliny ozdobne z liści'),
	(102, 'Rośliny ozdobne z kwiatów'),
	(103, 'Rośliny pnące i zwisające'),
	(104, 'Oplątwy'),
	(105, 'Sukulenty'),
	(106, 'Kaktusy');

INSERT INTO plants.species(id, fertilizing_frequency, misting_frequency, name, placement, soil, watering_frequency, category_id) VALUES
    (201, ?, ?, ?, ?, ?, ?, ?),
    (202, ?, ?, ?, ?, ?, ?, ?);