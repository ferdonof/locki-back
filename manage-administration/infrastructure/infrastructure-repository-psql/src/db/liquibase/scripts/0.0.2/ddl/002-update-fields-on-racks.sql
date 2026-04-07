--liquibase formatted sql
--changeset pmfernan@gmail.com:racks_administration_0.0.2_0
--comment: UPDATE fields on racks table

ALTER TABLE racks RENAME COLUMN number TO serial;
ALTER TABLE racks add COLUMN size INT NOT NULL default 0;

--rollback ALTER TABLE racks RENAME COLUMN serial TO number;
--rollback ALTER TABLE racks drop COLUMN size;
