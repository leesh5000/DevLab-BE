package com.leesh.devlab.domain.commentlike;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments_likes", indexes = {
        @Index(columnList = "comment_id"),
        @Index(columnList = "member_id")
})
@Entity
public class CommentLike extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "comment_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Comment comment;


    @Column(name = "value", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentLike commentLike)) return false;
        return id != null && id.equals(commentLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
