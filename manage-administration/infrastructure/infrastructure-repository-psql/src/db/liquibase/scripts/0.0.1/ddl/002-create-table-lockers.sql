--liquibase formatted sql
--changeset pmfernan@gmail.com:lockers_administration_0.0.1_0
--comment: CREATE lockers table

CREATE TABLE lockers (
                       id UUID PRIMARY KEY NOT NULL,
                       rack_id UUID,
                       status VARCHAR(15) NOT NULL,
                       latch_status VARCHAR(15) NOT NULL,
                       user_id UUID,
                       version BIGINT NOT NULL DEFAULT 1,
                       created_at TIMESTAMPTZ(6) NOT NULL,
                       updated_at TIMESTAMPTZ(6)
);


--rollback DROP TABLE lockers;
