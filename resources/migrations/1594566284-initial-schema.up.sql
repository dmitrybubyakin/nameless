CREATE TABLE IF NOT EXISTS chat
(id bigserial,
 url varchar(255) NOT NULL PRIMARY KEY,
 content text NOT NULL,
 author varchar NOT NULL,
 created_at timestamp NOT NULL DEFAULT now());

Create index url_index on chat(url);
