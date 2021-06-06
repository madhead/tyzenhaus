INSERT INTO group_config ("id", "invited_by", "invited_at", "language", "members")
VALUES (1, 1, '1995-08-12 00:00:00 +03:00', 'en', NULL),
       (2, 2, '1995-08-12 00:00:00 +03:00', NULL, NULL),
       (3, 3, NULL, NULL, NULL),
       (4, NULL, NULL, NULL, NULL),
       (5, 1, '1995-08-12 00:00:00 +03:00', 'en', '{1, 2, 3}');

INSERT INTO dialog_state ("group_id", "user_id", "state")
VALUES (1, 1, '{
  "groupId": 1,
  "userId": 1,
  "type": "ChangingLanguage"
}'::JSONB)
