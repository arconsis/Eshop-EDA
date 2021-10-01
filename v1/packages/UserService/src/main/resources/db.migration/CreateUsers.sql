CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR(200) NOT NULL,
    last_name  VARCHAR(200) NOT NULL,
    e_mail     VARCHAR(200) NOT NULL,
    password   VARCHAR(200) NOT NULL,
    username   VARCHAR(200) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP

)