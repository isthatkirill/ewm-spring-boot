INSERT INTO locations (lat, lon) VALUES
(10.10, 20.20), -- id=1
(20.20, 30.30), -- id=2
(30.30, 40.40), -- id=3
(40.40, 50.50), -- id=4
(50.50, 60.60), -- id=5
(60.60, 70.70), -- id=6
(70.70, 80.80); -- id=7

INSERT INTO categories (name) VALUES
('category_one'), -- id=1
('category_two'), -- id=2
('category_three'), -- id=3
('category_four'); -- id=4

INSERT INTO users (name, email) VALUES
('katya', 'katya@yandex.ru'), -- id=1
('vasya', 'vasya@yandex.ru'), -- id=2
('petya', 'petya@yandex.ru'), -- id=3
('lena', 'lena@yandex.ru'), -- id=4
('zhenya', 'zhenya@yandex.ru'); -- id=5

INSERT INTO events (annotation, created_on, description, event_date, paid, participant_limit, request_moderation, published_on, title, state, location_id, initiator_id, category_id) VALUES
('java', NOW(), 'java conference', NOW() + INTERVAL '3' DAY, true, 2, true, null, 'java conference title', 'PENDING', 1, 1, 1), -- id=1
('c++', NOW(), 'c++ conference', NOW() + INTERVAL '4' DAY, true, 2, false, NOW(), 'c++ conference title', 'PUBLISHED', 2, 1, 1), -- id=2
('NASM', NOW(), 'NASM conference', NOW() + INTERVAL '5' DAY, true, 3, true, NULL, 'NASM conference title', 'CANCELED', 3, 1, 2), -- id=3
('Prolog', NOW(), 'Prolog conference', NOW() + INTERVAL '5' DAY, false, 3, false, NOW(), 'Prolog conference title', 'PUBLISHED', 4, 1, 2), -- id=4
('Lisp', NOW(), 'Lisp conference', NOW() + INTERVAL '5' DAY, false, 3, true, null, 'Lisp conference title', 'PENDING', 5, 1, 2), -- id=5
('CSS', NOW(), 'CSS conference', NOW() + INTERVAL '5' DAY, false, 2, false, NOW(), 'CSS conference title', 'PUBLISHED', 6, 2, 3), -- id=6
('Javascript', NOW(), 'Javascript conference', NOW() + INTERVAL '5' DAY, false, 2, false, NOW(), 'Javascript conference title', 'PUBLISHED', 6, 3, 3), -- id=7
('HTML', NOW(), 'HTML conference', NOW() + INTERVAL '5' DAY, false, 4, false, NOW(), 'HTML conference title', 'PUBLISHED', 6, 4, 3), -- id=8
('Spring Data JPA', NOW(), 'Spring Data JPA conference', NOW() + INTERVAL '6' DAY, true, 4, true, null, 'Spring Data JPA conference title', 'PENDING', 7, 4, 4), -- id=9
('Spring Security', NOW(), 'Spring Security conference', NOW() + INTERVAL '7' DAY, true, 4, true, null, 'Spring Security conference title', 'PENDING', 7, 5, 4), -- id=10
('Spring Boot', NOW(), 'Spring Boot conference', NOW() + INTERVAL '8' DAY, true, 4, true, null, 'Spring Boot conference title', 'PENDING', 7, 5, 4), -- id=11
('Spring MVC', NOW(), 'Spring MVC conference', NOW() + INTERVAL '8' DAY, true, 1000, true, null, 'Spring MVC conference title', 'CANCELED', 7, 5, 4); -- id=12
