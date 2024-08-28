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
                        client_id INTEGER NOT NULL,
                        bank TEXT,
                        transaction_type TEXT,
                        quantity INTEGER,
                        price FLOAT,
                        transaction_amount FLOAT,
                        created_at TIMESTAMP
);
ALTER TABLE transactions REPLICA IDENTITY FULL;