package com.leesh.devlab.domain.member;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.member.constant.OauthType;
import com.leesh.devlab.domain.member.constant.Role;
import com.leesh.devlab.domain.post.Post;
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
    private String username;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 255, nullable = true)
    private String password;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_type", nullable = false, length = 10)
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

}
