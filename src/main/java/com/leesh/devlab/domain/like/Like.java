package com.leesh.devlab.domain.like;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes", indexes = {
        @Index(columnList = "post_id"),
        @Index(columnList = "comment_id"),
        @Index(columnList = "member_id")
})
@Entity
public class Like extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "comment_id", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Comment comment;

    @Column(name = "value", nullable = false, columnDefinition = "TINYINT(1) default 1")
    private boolean value = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Like like)) return false;
        return id != null && id.equals(like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /* 생성 메서드 */
    @Builder
    private Like(Member member, Post post, Comment comment) {
        this.member = member;
        this.post = post;
        this.comment = comment;
    }
}
