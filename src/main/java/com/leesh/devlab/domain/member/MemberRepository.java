package com.leesh.devlab.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("select m from Member m where m.refreshToken.value = :refreshToken")
    Optional<Member> findByRefreshToken(@Param("refreshToken") String refreshToken);

    boolean existsByLoginIdOrNickname(String email, String nickname);

    Optional<Member> findByOauthId(String id);

    Optional<Member> findByLoginId(String id);
}
