--liquibase formatted sql
--changeset pmfernan@gmail.com:users_administration_0.0.2_0
--comment: CREATE fees table

CREATE TABLE fees (
                  id UUID PRIMARY KEY NOT NULL,
                  locker_size VARCHAR(10) NOT NULL,
                  country VARCHAR(100) NOT NULL,
                  currency VARCHAR(150) NOT NULL,
                  amount Numeric(12, 2) NOT NULL,
                  version BIGINT NOT NULL DEFAULT 1,
                  created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
                  updated_at TIMESTAMP(6) WITH TIME ZONE
);

ALTER TABLE fees ADD CONSTRAINT uk_fees_locker_size_country_currency UNIQUE (locker_size ,country, currency);

--rollback DROP TABLE fees;
--rollback ALTER TABLE fees DROP CONSTRAINT uk_fees_locker_size_country_currency;

