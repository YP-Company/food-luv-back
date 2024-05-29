package com.youngpotato.foodluv.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Member findByEmail(String email);
}
