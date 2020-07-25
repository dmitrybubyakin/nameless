CREATE TABLE IF NOT EXISTS chat
(id bigserial PRIMARY KEY,
 url varchar(255) NOT NULL,
 data text NOT NULL,
 owner varchar NOT NULL,
 type varchar NOT NULL,
 created_at timestamp NOT NULL DEFAULT now());

Create index chat_url_index on chat(url);
