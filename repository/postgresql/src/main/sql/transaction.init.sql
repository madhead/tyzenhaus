CREATE TABLE transaction
(
    id         BIGSERIAL PRIMARY KEY,
    group_id   BIGINT         NOT NULL,
    payer      BIGINT         NOT NULL,
    recipients BIGINT[]       NOT NULL,
    amount     NUMERIC(15, 6) NOT NULL,
    currency   VARCHAR(128),
    timestamp  TIMESTAMP WITH TIME ZONE
);
