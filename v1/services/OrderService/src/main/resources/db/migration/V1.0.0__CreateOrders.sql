CREATE TYPE order_status AS ENUM (
    'PENDING',
    'VALID',
    'OUT_OF_STOCK',
    'PAID',
    'OUT_FOR_SHIPMENT',
    'COMPLETED',
    'PAYMENT_FAILED',
    'CANCELLED',
    'REFUNDED'
);

CREATE TABLE orders
(
    id         UUID PRIMARY KEY,
    user_id    UUID         NOT NULL,
    status     order_status NOT NULL,
    amount     NUMERIC      NOT NULL,
    currency   VARCHAR      NOT NULL,
    product_id VARCHAR      NOT NULL,
    quantity   INTEGER      NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
