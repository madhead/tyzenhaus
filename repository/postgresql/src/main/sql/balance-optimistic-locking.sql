DELETE
FROM balance a USING balance b
WHERE a.group_id = b.group_id
  AND a.version
    < b.version;

ALTER TABLE balance
DROP
CONSTRAINT balance_pkey;

ALTER TABLE balance
    ADD CONSTRAINT balance_pkey PRIMARY KEY (group_id);
