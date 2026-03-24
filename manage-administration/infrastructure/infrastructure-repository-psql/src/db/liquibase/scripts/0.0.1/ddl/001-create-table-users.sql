--liquibase formatted sql
--changeset pmfernan@gmail.com:users_administration_0.0.1_0
--comment: CREATE users table

CREATE TABLE users (
                      id UUID PRIMARY KEY NOT NULL,
                      name VARCHAR(100) NOT NULL,
                      email VARCHAR(150) NOT NULL,
                      phone VARCHAR(50) NOT NULL,
                      version BIGINT NOT NULL DEFAULT 1,
                      created_at TIMESTAMPTZ(6) NOT NULL,
                      updated_at TIMESTAMPTZ(6)
);

CREATE INDEX idx_users_email ON users (email);
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);

--rollback DROP TABLE users;
--rollback DROP INDEX idx_users_email;
--rollback ALTER TABLE users DROP CONSTRAINT uk_users_email;

