DROP TABLE IF EXISTS categories, users, locations, events, participation_requests, compilations, events_compilations CASCADE;

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGSERIAL PRIMARY KEY,
    lat REAL NOT NULL,
    lon REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGSERIAL PRIMARY KEY,
    annotation         VARCHAR(255) ,
    description        VARCHAR(255) ,
    event_date         TIMESTAMP ,
    paid               BOOLEAN ,
    participant_limit  BIGINT ,
    request_moderation BOOLEAN,
    title              VARCHAR(255) ,
    state              VARCHAR(255) ,
    created_on         TIMESTAMP ,
    published_on       TIMESTAMP,
    category_id        BIGINT REFERENCES categories (id) ON DELETE CASCADE,
    location_id        BIGINT REFERENCES locations (id) ON DELETE CASCADE,
    initiator_id       BIGINT REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGSERIAL PRIMARY KEY,
    created      TIMESTAMP    NOT NULL,
    status       VARCHAR(255) NOT NULL,
    event_id     BIGINT REFERENCES events (id) ON DELETE CASCADE,
    requester_id BIGINT REFERENCES requests (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGSERIAL PRIMARY KEY,
    pinned BOOLEAN     NOT NULL,
    title  VARCHAR(50) NOT NULL
--     event_id BIGINT REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    compilation_id BIGINT REFERENCES compilations (id),
    event_id       BIGINT REFERENCES events (id),
    PRIMARY KEY (compilation_id, event_id)
);