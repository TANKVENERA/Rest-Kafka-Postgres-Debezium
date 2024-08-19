DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS client_transaction;

CREATE TABLE client (
                id SERIAL PRIMARY KEY,
                client_id INTEGER NOT NULL UNIQUE,
                email TEXT,
                first_name TEXT,
                last_name TEXT
);
ALTER TABLE client REPLICA IDENTITY FULL;

CREATE TABLE client_transaction (
                        id SERIAL PRIMARY KEY,
                        client_id INTEGER NOT NULL,
                        bank TEXT,
                        transaction_type TEXT,
                        quantity INTEGER,
                        price FLOAT,
                        transaction_amount FLOAT,
                        created_at TIMESTAMP
);
ALTER TABLE client_transaction REPLICA IDENTITY FULL;

ALTER TABLE client_transaction
    ADD CONSTRAINT fk_client_id FOREIGN KEY (client_id) REFERENCES client(client_id);