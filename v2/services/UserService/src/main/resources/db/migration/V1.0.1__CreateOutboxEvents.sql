CREATE TABLE users_outbox_events
(
    id              uuid primary key,
    aggregate_type  varchar(255) not null,
    aggregate_id    varchar(255) not null,
    type            varchar(255) not null,
    payload         text not null,
    created_at      timestamp,
    updated_at      timestamp
)
