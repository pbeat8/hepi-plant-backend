INSERT INTO plants.categories(id, name)
	VALUES (?, ?);

INSERT INTO plants.species(id, fertilizing_frequency, misting_frequency, name, placement, soil, watering_frequency, category_id)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?);