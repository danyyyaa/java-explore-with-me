DROP TABLE IF EXISTS endpoint_hits CASCADE;

CREATE TABLE IF NOT EXISTS endpoint_hits
(
    id        BIGSERIAL PRIMARY KEY,
    app       VARCHAR(255) NOT NULL,
    uri       VARCHAR(255) NOT NULL,
    ip        VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP    NOT NULL
);
