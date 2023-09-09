package com.leesh.devlab.domain.comment;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments", indexes = {
        @Index(columnList = "member_id"),
        @Index(columnList = "created_at")
})
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean deleted = false;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;

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

}
