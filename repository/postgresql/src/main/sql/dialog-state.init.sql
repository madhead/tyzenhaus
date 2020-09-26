CREATE TABLE dialog_state
(
    group_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    state    JSONB,
    PRIMARY KEY (group_id, user_id)
);
