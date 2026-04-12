--liquibase formatted sql
--changeset pmfernan@gmail.com:manage_reservations_0.0.2_3
--comment: UPDATE reservations table

ALTER TABLE reservations ADD COLUMN location_id UUID NOT NULL;
ALTER TABLE reservations ADD CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES locations(id);
ALTER TABLE reservations DROP COLUMN address;
ALTER TABLE reservations DROP COLUMN city;
ALTER TABLE reservations DROP COLUMN country;
ALTER TABLE reservations DROP COLUMN zip_code;


--rollback ALTER TABLE reservations ADD COLUMN address VARCHAR(255) NOT NULL;
--rollback ALTER TABLE reservations ADD COLUMN city VARCHAR(100) NOT NULL;
--rollback ALTER TABLE reservations ADD COLUMN country VARCHAR(100) NOT NULL;
--rollback ALTER TABLE reservations ADD COLUMN zip_code VARCHAR(20) NOT NULL;
--rollback ALTER TABLE reservations DROP COLUMN location_id;