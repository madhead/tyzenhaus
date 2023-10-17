CREATE TABLE api_token
(
    token       UUID PRIMARY KEY,
    group_id    BIGINT                   NOT NULL,
    scope       VARCHAR(128)             NOT NULL,
    valid_until TIMESTAMP WITH TIME ZONE NOT NULL
);
