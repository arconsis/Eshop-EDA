CREATE TYPE aggregate_type AS ENUM (
    'ORDER'
);

CREATE TABLE outbox_events
(
    id              UUID PRIMARY KEY,
    aggregate_type  aggregate_type NOT NULL,
    aggregate_id    UUID NOT NULL,
    payload         JSONB NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
)
