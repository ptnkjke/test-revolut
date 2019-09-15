CREATE TABLE IF NOT EXISTS account
(
    id     varchar(200),
    amount decimal,
    CONSTRAINT PK PRIMARY KEY (id)
);

DELETE from account;