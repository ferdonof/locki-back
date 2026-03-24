--liquibase formatted sql
--changeset pmfernan@gmail.com:locations_administration_0.0.1_0
--comment: CREATE locations table

CREATE TABLE locations (
                       id UUID PRIMARY KEY NOT NULL,
                       city VARCHAR(100),
                       country VARCHAR(100),
                       description VARCHAR(255),
                       status VARCHAR(15) NOT NULL,
                       latitude DOUBLE PRECISION,
                       longitude DOUBLE PRECISION,
                       version INT4 NOT NULL DEFAULT 1,
                       created_at TIMESTAMPTZ(6) NOT NULL,
                       updated_at TIMESTAMPTZ(6)
);


--rollback DROP TABLE locations;
