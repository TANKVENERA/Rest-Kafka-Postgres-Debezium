DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS transactions;

CREATE TABLE clients (
                client_id INTEGER PRIMARY KEY,
                email TEXT,
                first_name TEXT,
                last_name TEXT
);
ALTER TABLE clients REPLICA IDENTITY FULL;

CREATE TABLE transactions (
                        id SERIAL PRIMARY KEY,
                        bank TEXT,
                        transaction_type TEXT NOT NULL,
                        quantity INTEGER,
                        price FLOAT,
                        transaction_amount FLOAT,
                        created_at TIMESTAMP,
                        client_id INTEGER NOT NULL
);
ALTER TABLE transactions REPLICA IDENTITY FULL;