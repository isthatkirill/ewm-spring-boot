insert into requests (requester_id, event_id, status, created) values
(1, 6, 'PENDING', NOW()), -- id=1
(2, 6, 'PENDING', NOW()), -- id=2
(2, 2, 'CONFIRMED', NOW()), -- id=3
(3, 2, 'CONFIRMED', NOW()), -- id=4
(5, 8, 'REJECTED', NOW()), -- id=5
(1, 8, 'CONFIRMED', NOW()), -- id=6
(3, 8, 'CONFIRMED', NOW()); -- id=7