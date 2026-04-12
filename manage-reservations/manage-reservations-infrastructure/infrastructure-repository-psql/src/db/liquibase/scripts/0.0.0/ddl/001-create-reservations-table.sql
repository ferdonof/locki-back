--liquibase formatted sql
--changeset pmfernan@gmail.com:manage_reservations_0.0.1_0
--comment: CREATE racked_lockers table

CREATE TABLE reservations (
             id UUID PRIMARY KEY NOT NULL,
             user_id UUID NOT NULL,
             locker_id UUID NOT NULL,
             rack_id UUID NOT NULL,
             position INT NOT NULL,
             address VARCHAR(255) NOT NULL,
             city VARCHAR(100) NOT NULL,
             country VARCHAR(100) NOT NULL,
             zip_code VARCHAR(20) NOT NULL,
             currency VARCHAR(10) NOT NULL,
             price DECIMAL(10, 2) NOT NULL,
             status VARCHAR(10) NOT NULL,
             start_date TIMESTAMP(6) WITH TIME ZONE NOT NULL,
             end_date TIMESTAMP(6) WITH TIME ZONE NOT NULL,
             version BIGINT NOT NULL DEFAULT 1,
             created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
             updated_at TIMESTAMP(6) WITH TIME ZONE
);

--rollback DROP TABLE reservations;