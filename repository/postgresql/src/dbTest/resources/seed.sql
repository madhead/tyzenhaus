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
VALUES (1, 1, 1, '{1,2,3}', 42.99, 'USD', 'Lunch', '1995-08-12 00:00:00 +03:00'),
       (2, 6, 1, '{1,2,3}', 42.99, 'USD', 'Breakfast', '1995-08-12 00:00:00 +03:00'),
       (3, 6, 2, '{1,2,3}', 42.99, 'USD', 'Lunch', '1995-08-12 00:00:00 +03:00'),
       (4, 6, 3, '{1,2,3}', 42.99, 'USD', 'Dinner', '1995-08-12 00:00:00 +03:00'),
       (5, 8, 1, '{1,2,3}', 42.99, 'USD', 'Breakfast', '1995-08-12 00:00:00 +03:00'),
       (6, 8, 2, '{1,2,3}', 42.99, 'USD', 'Lunch', '1995-08-12 00:00:00 +03:00'),
       (7, 8, 3, '{1,2,3}', 42.99, 'USD', 'Dinner', '1995-08-12 00:00:00 +03:00');

INSERT INTO "transaction" ("id", "group_id", "payer", "recipients", "amount", "currency", "title", "timestamp")
VALUES (101, 9, 1, '{1,2}', 10.00, 'USD', 'Coffee', '2024-01-01 12:00:00 +00:00'),
       (102, 9, 2, '{2,3}', 20.00, 'EUR', 'Lunch', '2024-01-02 12:00:00 +00:00'),
       (103, 9, 3, '{1,3}', 30.00, 'USD', 'Dinner', '2024-01-03 12:00:00 +00:00'),
       (104, 9, 1, '{2,3}', 40.00, 'EUR', 'Taxi', '2024-01-04 12:00:00 +00:00'),
       (105, 9, 2, '{1,2,3}', 50.00, 'USD', 'Hotel', '2024-01-05 12:00:00 +00:00');

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
VALUES ('00000000-0000-0000-0000-000000000000', 1, 'HISTORY', '9999-12-12 00:00:00 +00:00');
