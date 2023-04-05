CREATE TABLE account_db (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(6) NOT NULL UNIQUE
);

CREATE TABLE transaction_db (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date VARCHAR(10) NOT NULL,
    amount NUMERIC(7, 2) NOT NULL,
    type VARCHAR(10) NOT NULL,
    account_id BIGINT NOT NULL
);

ALTER TABLE transaction_db
    ADD FOREIGN KEY (account_id)
    REFERENCES account_db(id);