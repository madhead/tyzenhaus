CREATE TABLE group_config
(
    id         BIGINT PRIMARY KEY,
    invited_by BIGINT,
    invited_at TIMESTAMP WITH TIME ZONE,
    language   VARCHAR(8)
);
