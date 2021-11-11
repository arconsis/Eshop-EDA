CREATE TABLE inventory
(
    id         UUID PRIMARY KEY,
    product_id VARCHAR NOT NULL UNIQUE,
    stock      INTEGER NOT NULL CHECK ( stock >= 0 ),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
