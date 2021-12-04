CREATE TYPE payment_status AS ENUM (
    'SUCCEED',
    'FAILED'
);

CREATE TABLE payments
(
    id             UUID PRIMARY KEY,
    transaction_id UUID,
    user_id        UUID           NOT NULL,
    order_id       UUID           NOT NULL,
    status         payment_status NOT NULL,
    amount         NUMERIC        NOT NULL,
    currency       VARCHAR        NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
)
