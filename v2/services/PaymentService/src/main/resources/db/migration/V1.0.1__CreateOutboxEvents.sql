CREATE TABLE payments_outbox_events
(
    id              UUID PRIMARY KEY,
    aggregate_type  VARCHAR(255) NOT NULL,
    aggregate_id    VARCHAR(255) NOT NULL,
    type            VARCHAR(255) NOT NULL,
    payload         TEXT NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
)
