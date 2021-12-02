CREATE TABLE addresses
(
    id           UUID PRIMARY KEY,
    name         VARCHAR(200)               NOT NULL,
    address      VARCHAR(200)               NOT NULL,
    house_number VARCHAR(200)               NOT NULL,
    country_code VARCHAR(2)                 NOT NULL,
    postal_code  VARCHAR(10)                NOT NULL,
    city         VARCHAR(200)               NOT NULL,
    phone        VARCHAR(50)                NOT NULL,
    user_id      UUID REFERENCES users (id) NOT NULL
)