package com.leesh.devlab.domain.member;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.member.constant.OauthType;
import com.leesh.devlab.domain.member.constant.Role;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.external.OauthMemberInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members", indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "email"),
        @Index(columnList = "created_at")
})
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 255, nullable = true)
    private String password;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role = Role.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_type", nullable = true, length = 10)
    private OauthType oauthType;

    @Column(name = "profile_img_url", length = 255, nullable = true)
    private String profileImgUrl;

    @Column(name = "refresh_token", length = 255, nullable = true)
    private String refreshToken;

    @Column(name = "refresh_token_expired_at", nullable = true)
    private LocalDateTime refreshTokenExpiredAt;

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
    public static Member createMember(String name, String email, OauthType oauthType) {

        Member member = new Member();
        member.name = name;
        member.email = email;
        member.oauthType = oauthType;

        return member;
    }

    /* 도메인 비즈니스 로직 */

    /**
     * 탈퇴한 유저를 재가입 시키는 매소드
     * @param memberInfo
     */
    public void reRegister(OauthMemberInfo memberInfo) {

        // 기존의 소셜 로그인 정보 제공 업체를 재가입을 시도한 업체로 변경한다.
        this.oauthType = memberInfo.getOauthType();
        this.deleted = false;
        this.name = memberInfo.getName();
        this.email = memberInfo.getEmail();
        this.profileImgUrl = memberInfo.getProfileImgUrl();

    }

    /**
     * 소셜 로그인을 시도한 유저가 올바른 소셜 업체로 로그인 했는지 체크하는 메서드
     * @param oauthType
     */
    public void checkValidOauthType(OauthType oauthType) {
        OauthType.isValidOauthType(this.oauthType, oauthType);
    }

    /* Business Logic */

}
