CREATE TABLE IF NOT EXISTS locations
(
    id  BIGSERIAL PRIMARY KEY,
    lat REAL NOT NULL,
    lon REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGSERIAL PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    description        VARCHAR(7000) NOT NULL,
    event_date         TIMESTAMP     NOT NULL,
    paid               BOOLEAN       NOT NULL,
    participant_limit  BIGINT        NOT NULL,
    request_moderation BOOLEAN       NOT NULL,
    title              VARCHAR(120)  NOT NULL,
    state              VARCHAR(9)    NOT NULL,
    created_on         TIMESTAMP     NOT NULL,
    published_on       TIMESTAMP,
    comments           BIGINT        NOT NULL,
    category_id        BIGINT REFERENCES categories (id) ON DELETE CASCADE,
    location_id        BIGINT REFERENCES locations (id) ON DELETE CASCADE,
    initiator_id       BIGINT REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGSERIAL PRIMARY KEY,
    created      TIMESTAMP  NOT NULL,
    status       VARCHAR(9) NOT NULL,
    event_id     BIGINT REFERENCES events (id) ON DELETE CASCADE,
    requester_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_request UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGSERIAL PRIMARY KEY,
    pinned BOOLEAN     NOT NULL,
    title  VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    compilation_id BIGINT REFERENCES compilations (id) ON DELETE CASCADE,
    event_id       BIGINT REFERENCES events (id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGSERIAL PRIMARY KEY,
    content   VARCHAR(3000) NOT NULL,
    created   TIMESTAMP     NOT NULL,
    updated   TIMESTAMP,
    event_id  BIGINT REFERENCES events (id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users (id) ON DELETE CASCADE
);

