package com.youngpotato.foodluv.domain.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * JpaRepository를 사용하는 것 처럼, CrudRepository 인터페이스를 상속받는다.
 * @Id 또는 @Indexed 어노테이션을 적용한 프로퍼티들만 CrudRepository가 제공하는 findBy~ 구문을 사용할 수 있다.
 */

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAuthId(String authId);
}
