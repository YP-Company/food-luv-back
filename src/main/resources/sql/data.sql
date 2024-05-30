-- 회원 데이터
INSERT INTO member (cre_time, cre_member_id, upd_time, upd_member_id, email, password, nickname, roles) VALUES
(now(), 0, now(), 0, 'test1@test.com', '$2a$10$pnSVV23xWxI24/7uR3U.e.2LJIvxIpXxsG.Tbsp2324GQdDRPvLsG', '테스터1', 'ROLE_USER'),
(now(), 0, now(), 0, 'test2@test.com', '$2a$10$pnSVV23xWxI24/7uR3U.e.2LJIvxIpXxsG.Tbsp2324GQdDRPvLsG', '테스터2', 'ROLE_MANAGER'),
(now(), 0, now(), 0, 'test3@test.com', '$2a$10$pnSVV23xWxI24/7uR3U.e.2LJIvxIpXxsG.Tbsp2324GQdDRPvLsG', '테스터3', 'ROLE_ADMIN');