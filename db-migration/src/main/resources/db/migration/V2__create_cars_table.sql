CREATE TABLE IF NOT EXISTS car_types (
	id   INT8 PRIMARY KEY UNIQUE GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(30) NOT NULL);

CREATE TABLE IF NOT EXISTS cars (
	id                  INT8 PRIMARY KEY UNIQUE GENERATED ALWAYS AS IDENTITY,
	vin                 VARCHAR(17) UNIQUE NOT NULL,
	registration_number VARCHAR(10),
	brand               VARCHAR(30)        NOT NULL,
	model               VARCHAR(30)        NOT NULL,
	production_year     INT                NOT NULL,
	mileage             INT                NOT NULL,
	description         VARCHAR(250),
	type_id             INT8,
	CONSTRAINT fk_car_type FOREIGN KEY (type_id) REFERENCES car_types (id));
