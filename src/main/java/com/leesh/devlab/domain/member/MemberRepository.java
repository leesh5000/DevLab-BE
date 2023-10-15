package com.leesh.devlab.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.refreshToken.value = :refreshToken")
    Optional<Member> findByRefreshToken(@Param("refreshToken") String refreshToken);

    Optional<Member> findByOauthId(String id);

    Optional<Member> findByLoginId(String id);

    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);
}
