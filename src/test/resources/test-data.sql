MERGE INTO status
USING (VALUES (0, 'WAITING'), (1, 'APPROVED'), (2, 'REJECTED')) AS new_values (status_id, title)
ON status.status_id = new_values.status_id
WHEN NOT MATCHED THEN
INSERT (status_id, title) VALUES (new_values.status_id, new_values.title);

INSERT INTO users(name, email)
VALUES ('Олег','oleg@ya.ru'), ('Василий', 'vasayn@gmail.com'),('Алексей', 'Al@yandex.ru');

INSERT INTO items(owner, name, description, available)
VALUES (1, 'Шуруповерт', 'описание шуруповерта',true),
       (2, 'Дрель','описание дрели',true),
       (2, 'Молоток','описание молотка',true),
       (3, 'Палатка','описание полатки',true);

INSERT INTO bookings(user_id, item_id, start_time, end_time,status_id)
VALUES (1, 2, '2026-12-03T10:15:30','2026-12-05T10:15:30', 0),
       (2, 3, '2026-12-13T10:15:30','2026-12-16T10:15:30', 0),
       (3, 4, '2026-12-23T10:15:30','2026-12-27T10:15:30', 0)