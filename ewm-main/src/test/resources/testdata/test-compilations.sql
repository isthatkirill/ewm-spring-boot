insert into compilations (title, pinned) values
('high-level pl compilation', true), -- id=1
('low-level pl compilation', true), -- id=2
('spring compilation', true), -- id=3
('frontend compilation', false), -- id=4
('backend compilation', false), -- id=5
('web compilation', false), -- id=6
('empty compilation', false); -- id = 7

insert into compilations_events values
(1, 1), (1, 2), (1, 7),
(2, 2), (2, 3), (2, 4), (2, 5),
(3, 9), (3, 10), (3, 11), (3, 12),
(4, 6), (4, 7), (4, 8),
(5, 1), (5, 2), (5, 9), (5, 10), (5, 11), (5, 12),
(6,  1), (6,  6), (6,  7), (6,  8), (6,  9), (6,  10), (6,  11), (6,  12);
