INSERT INTO "group_config" ("id", "invited_by", "invited_at", "language", "members")
VALUES (1, 1, '1995-08-12 00:00:00 +03:00', 'en', NULL),
       (2, 2, '1995-08-12 00:00:00 +03:00', NULL, NULL),
       (3, 3, NULL, NULL, NULL),
       (4, NULL, NULL, NULL, NULL),
       (5, 1, '1995-08-12 00:00:00 +03:00', 'en', '{1, 2, 3}'),
       (6, 1, NULL, NULL, '{1, 2, 3}');

INSERT INTO "dialog_state" ("group_id", "user_id", "state")
VALUES (1, 1, '{
    "groupId": 1,
    "userId": 1,
    "messageId": 42,
    "type": "WaitingForAmount"
}'::JSONB);
INSERT INTO "dialog_state" ("group_id", "user_id", "state")
VALUES (6, 1, '{
    "groupId": 6,
    "userId": 1,
    "messageId": 42,
    "type": "WaitingForAmount"
}'::JSONB);
INSERT INTO "dialog_state" ("group_id", "user_id", "state")
VALUES (6, 2, '{
    "groupId": 6,
    "userId": 2,
    "messageId": 43,
    "type": "WaitingForAmount"
}'::JSONB);

INSERT INTO "transaction" ("id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp")
VALUES (1, 1, 1, '{1,2,3}', 42.99, 'USD', 'Lunch', '1995-08-12 00:00:00 +03:00');
INSERT INTO "transaction" ("id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp")
VALUES (2, 6, 1, '{1,2,3}', 42.99, 'USD', 'Breakfast', '1995-08-12 00:00:00 +03:00');
INSERT INTO "transaction" ("id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp")
VALUES (3, 6, 2, '{1,2,3}', 42.99, 'USD', 'Lunch', '1995-08-12 00:00:00 +03:00');
INSERT INTO "transaction" ("id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp")
VALUES (4, 6, 3, '{1,2,3}', 42.99, 'USD', 'Dinner', '1995-08-12 00:00:00 +03:00');

INSERT INTO balance ("group_id", "version", "balance")
VALUES (1, 2, '{
    "groupId": 1,
    "balance": {
        "EUR": {
            "1": "42.99",
            "2": "-42.99"
        },
        "USD": {
            "1": "-42.99",
            "2": "42.99"
        }
    }
}'::JSONB);
INSERT INTO balance ("group_id", "version", "balance")
VALUES (6, 1, '{
    "groupId": 6,
    "balance": {}
}'::JSONB);

INSERT INTO "api_token" ("token", "group_id", "scope", "valid_until")
VALUES ('00000000-0000-0000-0000-000000000000', 1, 'HISTORY', '9999-12-12 00:00:00');
