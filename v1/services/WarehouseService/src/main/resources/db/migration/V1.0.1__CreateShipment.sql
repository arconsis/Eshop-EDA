CREATE TYPE shipment_status AS ENUM (
    'PREPARING_SHIPMENT',
    'OUT_FOR_SHIPMENT',
    'SHIPPED'
    );

CREATE TABLE shipments
(
    id       UUID PRIMARY KEY,
    order_id UUID            NOT NULL UNIQUE,
    status   shipment_status NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)
