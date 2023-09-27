package com.leesh.devlab.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

    boolean existsByEmail(String email);

    boolean existsByNickname(String name);

    boolean existsByLoginIdOrNickname(String email, String nickname);

    Optional<Member> findByOauthId(String id);

    Optional<Member> findByLoginId(String id);
}
