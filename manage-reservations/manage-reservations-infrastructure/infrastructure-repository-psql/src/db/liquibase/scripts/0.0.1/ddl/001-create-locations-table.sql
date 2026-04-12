--liquibase formatted sql
--changeset pmfernan@gmail.com:manage_reservations_0.0.2_1
--comment: CREATE locations table

CREATE TABLE locations (
             id UUID PRIMARY KEY NOT NULL,
             location_id UUID NOT NULL,
             address VARCHAR(100) NOT NULL,
             city VARCHAR(100) NOT NULL,
             zip_code VARCHAR(15) NOT NULL,
             country VARCHAR(100) NOT NULL,
             description VARCHAR(255),
             lat Numeric(9, 6),
             lon Numeric(9, 6),
             version BIGINT NOT NULL DEFAULT 1,
             created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
             updated_at TIMESTAMP(6) WITH TIME ZONE
);

ALTER TABLE locations ADD CONSTRAINT unique_location_id UNIQUE (location_id);
ALTER TABLE locations ADD CONSTRAINT unique_address_city_zip_code_country UNIQUE (address, city, zip_code, country);

--rollback DROP TABLE locations;