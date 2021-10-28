CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name  VARCHAR NOT NULL,
    email      VARCHAR NOT NULL,
    password   VARCHAR NOT NULL,
    username   VARCHAR NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP

)