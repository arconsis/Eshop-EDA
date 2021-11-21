CREATE TYPE aggregate_type AS ENUM (
    'ORDER'
);

CREATE TABLE outbox_events
(
    id              UUID PRIMARY KEY,
    aggregateType   aggregate_type NOT NULL,
    aggregateId     UUID NOT NULL,
    payload         JSONB NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
)
