--liquibase formatted sql
--changeset pmfernan@gmail.com:lockers_administration_0.0.2_0
--comment: add column on lockers table

ALTER TABLE lockers add COLUMN size VARCHAR(10) default 'MEDIUM';

--rollback ALTER TABLE lockers drop COLUMN size;
