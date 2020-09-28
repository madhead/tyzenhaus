INSERT INTO group_config ("id", "invited_by", "invited_at", "language")
VALUES (1, 1, '1995-08-12 00:00:00', 'en'),
       (2, 2, '1995-08-12 00:00:00', NULL),
       (3, 3, NULL, NULL),
       (4, NULL, NULL, NULL);

INSERT INTO dialog_state ("group_id", "user_id", "state")
VALUES (1, 1, '{
	"groupId": 1,
	"userId": 1,
	"type": "ChangingLanguage"
}'::JSONB)
