-- 회원 데이터
INSERT INTO member (email, password, nickname, roles) VALUES
('test1@test.com', '$2a$10$pnSVV23xWxI24/7uR3U.e.2LJIvxIpXxsG.Tbsp2324GQdDRPvLsG', '테스터1', 'ROLE_USER'),
('test2@test.com', '$2a$10$pnSVV23xWxI24/7uR3U.e.2LJIvxIpXxsG.Tbsp2324GQdDRPvLsG', '테스터2', 'ROLE_MANAGER'),
('test3@test.com', '$2a$10$pnSVV23xWxI24/7uR3U.e.2LJIvxIpXxsG.Tbsp2324GQdDRPvLsG', '테스터3', 'ROLE_ADMIN');