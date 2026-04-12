--liquibase formatted sql
--changeset pmfernan@gmail.com:manage_reservations_0.0.2_2
--comment: UPDATE racked_lockers table

ALTER TABLE racked_lockers ADD COLUMN location_id UUID NOT NULL;
ALTER TABLE racked_lockers ADD CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES locations(id);
ALTER TABLE racked_lockers DROP COLUMN address;
ALTER TABLE racked_lockers DROP COLUMN city;
ALTER TABLE racked_lockers DROP COLUMN country;
ALTER TABLE racked_lockers DROP COLUMN zip_code;
ALTER TABLE racked_lockers DROP COLUMN lat;
ALTER TABLE racked_lockers DROP COLUMN lon;

--rollback ALTER TABLE racked_lockers ADD COLUMN address VARCHAR(255) NOT NULL;
--rollback ALTER TABLE racked_lockers ADD COLUMN city VARCHAR(100) NOT NULL;
--rollback ALTER TABLE racked_lockers ADD COLUMN country VARCHAR(100) NOT NULL;
--rollback ALTER TABLE racked_lockers ADD COLUMN zip_code VARCHAR(20) NOT NULL;
--rollback ALTER TABLE racked_lockers ADD COLUMN lat DECIMAL(10, 6) NOT NULL;
--rollback ALTER TABLE racked_lockers ADD COLUMN lon DECIMAL(10, 6) NOT NULL;
--rollback ALTER TABLE racked_lockers DROP COLUMN location_id;


