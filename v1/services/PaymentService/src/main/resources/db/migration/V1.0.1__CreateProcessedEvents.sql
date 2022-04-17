CREATE TABLE processed_events
(
    event_id        UUID PRIMARY KEY,
    processed_at    TIMESTAMP NOT NULL,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
)