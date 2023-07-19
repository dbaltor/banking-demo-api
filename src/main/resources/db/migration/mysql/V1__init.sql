CREATE TABLE IF NOT EXISTS account_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    number VARCHAR(6) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transaction_db (
    id BIGINT NOT NULL AUTO_INCREMENT,
    date VARCHAR(10) NOT NULL,
    amount NUMERIC(7, 2) NOT NULL,
    type VARCHAR(10) NOT NULL,
    account_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id)
        REFERENCES account_db(id)
        ON UPDATE CASCADE
);