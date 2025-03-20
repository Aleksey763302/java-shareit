MERGE INTO status
USING (VALUES (0, 'WAITING'), (1, 'APPROVED'), (2, 'REJECTED')) AS new_values (status_id, title)
ON status.status_id = new_values.status_id
WHEN NOT MATCHED THEN
INSERT (status_id, title) VALUES (new_values.status_id, new_values.title);