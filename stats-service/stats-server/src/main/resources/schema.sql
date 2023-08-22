CREATE TABLE IF NOT EXISTS endpoint_hits
(
    id        BIGSERIAL PRIMARY KEY,
    app       VARCHAR     NOT NULL,
    uri       VARCHAR     NOT NULL,
    ip        VARCHAR     NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
