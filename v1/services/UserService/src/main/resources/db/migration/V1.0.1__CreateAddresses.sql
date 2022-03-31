CREATE TYPE valid_country_code AS ENUM ('DE', 'GR', 'UK');

CREATE TABLE addresses
(
    id                    UUID PRIMARY KEY,
    name                  VARCHAR(200)               NOT NULL,
    address               VARCHAR(200)               NOT NULL,
    house_number          VARCHAR(200)               NOT NULL,
    country_code          VALID_COUNTRY_CODE         NOT NULL,
    postal_code           VARCHAR(10)                NOT NULL,
    city                  VARCHAR(200)               NOT NULL,
    phone                 VARCHAR(50)                NOT NULL,
    is_billing            BOOLEAN                    NOT NULL,
    is_preferred_shipping BOOLEAN                    NOT NULL,
    user_id               UUID REFERENCES users (id) NOT NULL
)