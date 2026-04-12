--liquibase formatted sql
--changeset pmfernan@gmail.com:manage_reservations_0.0.2_1
--comment: CREATE locations table

CREATE TABLE locations (
             id UUID PRIMARY KEY NOT NULL,
             address VARCHAR(100),
             city VARCHAR(100),
             zip_code VARCHAR(15),
             country VARCHAR(100),
             description VARCHAR(255),
             lat Numeric(9, 6),
             lon Numeric(9, 6),
             version BIGINT NOT NULL DEFAULT 1,
             created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
             updated_at TIMESTAMP(6) WITH TIME ZONE
);


--rollback DROP TABLE locations;