-- 회원 테이블
CREATE TABLE member (
    member_id bigint NOT NULL AUTO_INCREMENT,
    cre_time datetime(6) NOT NULL,
    cre_member_id bigint NOT NULL,
    upd_time datetime(6) NOT NULL,
    upd_member_id bigint DEFAULT NULL,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    nickname varchar(255) NOT NULL,
    role varchar(255) NOT NULL,
    provider varchar(255) DEFAULT NULL,
    provider_id varchar(255) DEFAULT NULL,
    PRIMARY KEY (`member_id`)
);
COMMENT ON COLUMN member.member_id IS '회원 고유 식별자';
COMMENT ON COLUMN member.cre_time IS '생성시간';
COMMENT ON COLUMN member.cre_member_id IS '생성자 고유 식별자';
COMMENT ON COLUMN member.upd_time IS '수정시간';
COMMENT ON COLUMN member.upd_member_id IS '수정자 고유 식별자';
COMMENT ON COLUMN member.email IS '이메일';
COMMENT ON COLUMN member.password IS '비밀번호';
COMMENT ON COLUMN member.nickname IS '별명';
COMMENT ON COLUMN member.role IS '권한';
COMMENT ON COLUMN member.provider IS '제공자';
COMMENT ON COLUMN member.provider_id IS '제공자 고유 식별자';