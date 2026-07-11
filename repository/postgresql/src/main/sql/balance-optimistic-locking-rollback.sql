ALTER TABLE balance
DROP
CONSTRAINT balance_pkey;

ALTER TABLE balance
    ADD CONSTRAINT balance_pkey PRIMARY KEY (group_id, version);
