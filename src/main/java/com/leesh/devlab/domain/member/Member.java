package com.leesh.devlab.domain.member;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.jwt.Token;
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
        @Index(columnList = "nickname"),
        @Index(columnList = "created_at")
})
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "login_id", length = 20, nullable = true, unique = true)
    private String loginId;

    @Column(name = "nickname", length = 10, nullable = false, unique = true)
    private String nickname;

    @Column(name = "security_code", length = 255, unique = true, nullable = true)
    private String securityCode;

    @Column(name = "password", length = 255, nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role = Role.MEMBER;

    @Embedded
    private Oauth oauth;

    @Embedded
    private RefreshToken refreshToken;

    @OrderBy("id")
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Post> posts = new ArrayList<>();

    @OrderBy("id")
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();

    @OrderBy("id")
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Like> likes = new ArrayList<>();

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
    public static Member of(OauthType oauthType, String id, String nickname) {

        Member member = new Member();

        member.nickname = nickname;
        member.oauth = new Oauth(oauthType, id);

        return member;
    }

    @Builder
    private Member(String nickname, String loginId, String password, boolean verified) {
        this.nickname = nickname;
        this.loginId = loginId;
        this.password = password;
        if (verified) {
            this.securityCode = UUID.randomUUID().toString();
        }
    }

    /* 도메인 비즈니스 로직 */
    public void updateRefreshToken(Token refreshToken) {
        this.refreshToken = new RefreshToken(refreshToken.getValue(), System.currentTimeMillis() + refreshToken.getExpiresIn());
    }

    public void logout() {
        this.refreshToken.expiration();
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public Comment comment(Post post, String contents) {

        Comment comment = Comment.builder()
                .contents(contents)
                .member(this)
                .post(post)
                .build();

        this.comments.add(comment);

        return comment;
    }

    public Like like(Post post) {
        Like like = Like.builder()
                .member(this)
                .post(post)
                .build();
        this.likes.add(like);
        post.getLikes().add(like);
        return like;
    }
    public Like like(Comment comment) {
        Like like = Like.builder()
                .member(this)
                .comment(comment)
                .build();
        this.likes.add(like);
        comment.getLikes().add(like);
        return like;
    }

}
