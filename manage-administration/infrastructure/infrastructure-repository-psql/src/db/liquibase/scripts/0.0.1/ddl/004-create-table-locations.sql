--liquibase formatted sql
--changeset pmfernan@gmail.com:locations_administration_0.0.1_0
--comment: CREATE locations table

CREATE TABLE locations (
                       id UUID PRIMARY KEY NOT NULL,
                       address VARCHAR(100),
                       city VARCHAR(100),
                       code VARCHAR(15),
                       country VARCHAR(100),
                       description VARCHAR(255),
                       status VARCHAR(15) NOT NULL,
                       latitude Numeric(9, 6),
                       longitude Numeric(9, 6),
                       version BIGINT NOT NULL DEFAULT 1,
                       created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                       updated_at TIMESTAMP(6) WITH TIME ZONE
);


--rollback DROP TABLE locations;
