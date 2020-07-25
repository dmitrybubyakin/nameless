CREATE TABLE IF NOT EXISTS room
(id bigserial PRIMARY KEY,
 url varchar(255) NOT NULL,
 host varchar NOT NULL,
 created_at timestamp NOT NULL DEFAULT now(),
 active bool);

Create index rooms_index on room(url);
