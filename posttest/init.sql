-- Drop tables if they exist
DROP TABLE IF EXISTS user_ticket CASCADE;

DROP TABLE IF EXISTS lottery CASCADE;

CREATE TABLE lottery (
    id SERIAL PRIMARY KEY,
    ticket VARCHAR(6) UNIQUE NOT NULL,
    price INT NOT NULL,
    amount INT NOT NULL
);

CREATE TABLE user_ticket (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(10) NOT NULL,
    lottery INT REFERENCES lottery(id) ON DELETE CASCADE
);