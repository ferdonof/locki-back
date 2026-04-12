--liquibase formatted sql
--changeset pmfernan@gmail.com:manage_reservations_0.0.1_0
--comment: CREATE racked_lockers table

CREATE TABLE racked_lockers (
              id UUID PRIMARY KEY NOT NULL,
              locker_id UUID NOT NULL,
              rack_id UUID NOT NULL,
              position INT NOT NULL,
              rack_status VARCHAR(20) NOT NULL,
              address VARCHAR(255) NOT NULL,
              city VARCHAR(100) NOT NULL,
              country VARCHAR(100) NOT NULL,
              zip_code VARCHAR(20) NOT NULL,
              lat DECIMAL(10, 6) NOT NULL,
              lon DECIMAL(10, 6) NOT NULL,
              locker_status VARCHAR(20) NOT NULL,
              size VARCHAR(20) NOT NULL,
              version BIGINT NOT NULL DEFAULT 1,
              created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
              updated_at TIMESTAMP(6) WITH TIME ZONE
);

--rollback DROP TABLE racked_lockers;