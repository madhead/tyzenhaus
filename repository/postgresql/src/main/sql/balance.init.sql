CREATE TABLE balance
(
    group_id BIGINT NOT NULL,
    version  BIGINT NOT NULL,
    balance  JSONB  NOT NULL,

    PRIMARY KEY (group_id, version)
);
