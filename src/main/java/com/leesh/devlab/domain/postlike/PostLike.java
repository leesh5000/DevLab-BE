package com.leesh.devlab.domain.postlike;

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
@Table(name = "posts_likes", indexes = {
        @Index(columnList = "post_id"),
        @Index(columnList = "member_id")
})
@Entity
public class PostLike extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;


    @Column(name = "value", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostLike postLike)) return false;
        return id != null && id.equals(postLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
