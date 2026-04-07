--liquibase formatted sql
--changeset pmfernan@gmail.com:lockers_administration_0.0.2_0
--comment: UPDATE fields on lockers table

ALTER TABLE lockers RENAME COLUMN number TO serial;
ALTER TABLE lockers add COLUMN position INT NOT NULL default 0;

--rollback ALTER TABLE lockers RENAME COLUMN serial TO number;
--rollback ALTER TABLE lockers drop COLUMN position;
