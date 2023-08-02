insert into endpoint_hits (app, ip, uri, created) values
('ewm', '1.1.1.1', '/test', NOW()),
('ewm', '1.1.1.1', '/test', NOW() - INTERVAL '1' DAY),
('ewm', '1.1.1.2', '/test', NOW() - INTERVAL '2' DAY),
('ewm', '1.1.1.2', '/test/another', NOW() - INTERVAL '1' DAY),
('ewm', '1.1.1.2', '/test/another', NOW() - INTERVAL '2' DAY),
('ewm', '1.1.1.3', '/test/another', NOW() - INTERVAL '1' DAY);