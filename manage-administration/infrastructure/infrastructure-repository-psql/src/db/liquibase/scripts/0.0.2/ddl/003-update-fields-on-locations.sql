--liquibase formatted sql
--changeset pmfernan@gmail.com:locations_administration_0.0.2_0
--comment: UPDATE fields on locations table

ALTER TABLE locations RENAME COLUMN latitude TO lat;
ALTER TABLE locations RENAME COLUMN longitude TO lon;

--rollback ALTER TABLE locations RENAME COLUMN lat TO latitude;
--rollback ALTER TABLE locations RENAME COLUMN lon TO longitude;
