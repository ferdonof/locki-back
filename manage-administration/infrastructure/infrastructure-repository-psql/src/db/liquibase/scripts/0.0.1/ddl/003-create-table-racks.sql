--liquibase formatted sql
--changeset pmfernan@gmail.com:racks_administration_0.0.1_0
--comment: CREATE racks table

CREATE TABLE racks (
                       id UUID PRIMARY KEY NOT NULL,
                       number INT NOT NULL,
                       status VARCHAR(15) NOT NULL,
                       location_id UUID,
                       version BIGINT NOT NULL DEFAULT 1,
                       created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                       updated_at TIMESTAMP(6) WITH TIME ZONE
);


--rollback DROP TABLE racks;
