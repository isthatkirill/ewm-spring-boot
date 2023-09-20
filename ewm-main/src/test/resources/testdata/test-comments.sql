insert into comments (message, created, author_id, event_id) values
('cannot wait for it', NOW() + INTERVAL '1' DAY, 5, 2), -- id=1
('c++ is my fav lang', NOW() + INTERVAL '2' DAY, 4, 2), -- id=2
('i will definitely come, c++ is my favourite programming language', NOW() + INTERVAL '2' DAY, 3, 2), -- id=3
('will we talking about c?', NOW() + INTERVAL '3' DAY, 4, 2), -- id=4
('or only c++?', NOW() + INTERVAL '3' DAY, 4, 2), -- id=5
('wait for good css conference', NOW() + INTERVAL '1' DAY, 1, 6), -- id=6
('i will come', NOW() + INTERVAL '2' DAY, 4, 6), -- id=7
('what is the cover charge?', NOW() + INTERVAL '1' DAY, 5, 6), -- id=8
('how long will it last?', NOW() + INTERVAL '1' DAY, 1, 9), -- id=9
('cool!', NOW() + INTERVAL '10' DAY, 2, 9), -- id=10
('nice!', NOW() + INTERVAL '11' DAY, 3, 9); -- id=11
