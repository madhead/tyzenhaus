INSERT INTO group_config ("id", "language")
VALUES (1, 'en'),
       (2, NULL);

INSERT INTO dialog_state ("group_id", "user_id", "state")
VALUES (1, 1, '{
	"groupId": 1,
	"userId": 1,
	"type": "ChangingLanguage"
}'::JSONB)
