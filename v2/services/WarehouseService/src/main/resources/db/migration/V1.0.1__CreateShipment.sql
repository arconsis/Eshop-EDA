CREATE TYPE shipment_status AS ENUM (
    'PREPARING_SHIPMENT',
    'SHIPPED',
    'DELIVERED',
    'CANCELLED'
    );

CREATE TABLE shipments
(
    id       UUID PRIMARY KEY,
    order_id UUID            NOT NULL UNIQUE,
    user_id UUID            NOT NULL,
    status   shipment_status NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
