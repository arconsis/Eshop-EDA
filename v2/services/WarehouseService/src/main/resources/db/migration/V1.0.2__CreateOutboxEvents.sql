CREATE TYPE aggregate_type AS ENUM (
    'SHIPMENT',
    'ORDER_VALIDATION'
);

CREATE TABLE outbox_events
(
    id              UUID PRIMARY KEY,
    aggregate_type  aggregate_type NOT NULL,
    aggregate_id    UUID NOT NULL UNIQUE,
    payload         JSONB NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
)
