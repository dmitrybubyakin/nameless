CREATE TABLE IF NOT EXISTS chat
(id bigserial PRIMARY KEY,
 uuid varchar(255) NOT NULL,
 message text NOT NULL,
 author varchar NOT NULL,
 created_at timestamp NOT NULL DEFAULT now());

Create index url_index on chat(uuid);
