CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name  VARCHAR NOT NULL,
    email      VARCHAR NOT NULL,
    username   VARCHAR NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_user_id
    ON users (user_id);