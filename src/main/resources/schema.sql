CREATE TABLE IF NOT EXISTS users (
user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
requestor_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
description VARCHAR NOT NULL,
created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
owner BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
name VARCHAR(255) NOT NULL,
description VARCHAR(1024) NOT NULL,
available BOOLEAN NOT NULL,
request_id BIGINT REFERENCES requests (request_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS response_to_requests (
request_id BIGINT NOT NULL REFERENCES requests (request_id) ON DELETE CASCADE,
item_id BIGINT NOT NULL REFERENCES items (item_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
item_id BIGINT NOT NULL REFERENCES items (item_id) ON DELETE CASCADE,
user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
description VARCHAR NOT NULL,
created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS status (
status_id BIGINT NOT NULL UNIQUE,
title VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings (
booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
item_id BIGINT NOT NULL REFERENCES items (item_id) ON DELETE CASCADE,
start_time TIMESTAMP NOT NULL,
end_time TIMESTAMP NOT NULL,
status_id BIGINT NOT NULL REFERENCES status(status_id) ON DELETE CASCADE
);