package com.leesh.devlab.domain.comment;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments", indexes = {
        @Index(columnList = "member_id"),
        @Index(columnList = "contents"),
        @Index(columnList = "post_id"),
        @Index(columnList = "created_at")
})
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;

    @OrderBy("id")
    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Like> likes = new ArrayList<>();

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Comment comment)) return false;
        return id != null && id.equals(comment.id);
    }

    /* 생성 메서드 */
    @Builder
    private Comment(String contents, Member member, Post post) {
        this.contents = contents;
        this.member = member;
        this.post = post;
    }
}
