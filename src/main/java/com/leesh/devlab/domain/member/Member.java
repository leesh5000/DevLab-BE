package com.leesh.devlab.domain.member;

import com.leesh.devlab.constant.Role;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.jwt.AuthToken;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members", indexes = {
        @Index(columnList = "login_id"),
        @Index(columnList = "nickname"),
        @Index(columnList = "created_at")
})
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "login_id", length = 255, nullable = true, unique = true)
    private String loginId;

    @Column(name = "nickname", length = 30, nullable = false, unique = true)
    private String nickname;

    @Column(name = "email", length = 255, unique = true, nullable = true)
    private String email;

    @Column(name = "password", length = 255, nullable = true)
    private String password;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role = Role.MEMBER;

    /**
     * 소셜 로그인을 시도한 유저의 소셜 업체 고유값 ID
     */
    @Column(name = "oauth_id", nullable = true, length = 255, unique = true)
    private String oauthId;

    @Column(name = "profile_img_url", length = 255, nullable = true)
    private String profileImgUrl;

    @Column(name = "refresh_token", length = 255, nullable = true)
    private String refreshToken;

    @Column(name = "refresh_token_expired_at", nullable = true)
    private Long refreshTokenExpiredAt;

    @Column(name = "email_verified", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean emailVerified = false;

    @OrderBy("id")
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final List<Post> posts = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Member)) return false;
        Member member = (Member) obj;

        return id != null && id.equals(member.id);
    }

    /* 생성 메서드 */
    public static Member of(String oauthId) {

        Member member = new Member();
        member.nickname = UUID.randomUUID().toString().split("-")[0];
        member.oauthId = oauthId;

        return member;
    }

    @Builder
    private Member(String nickname, String loginId, String password) {
        this.nickname = nickname;
        this.loginId = loginId;
        this.password = password;
    }

    /* 도메인 비즈니스 로직 */
    public void reRegister() {
        this.deleted = false;
    }

    public void updateRefreshToken(AuthToken refreshToken) {
        this.refreshToken = refreshToken.getValue();
        this.refreshTokenExpiredAt = System.currentTimeMillis() + TokenType.REFRESH.getExpiresInMills();
    }

    public void logout() {
        this.refreshTokenExpiredAt = System.currentTimeMillis();
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void verifyEmail(String email) {
        this.email = email;
        this.emailVerified = true;
    }
}
