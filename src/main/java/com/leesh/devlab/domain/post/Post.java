package com.leesh.devlab.domain.post;

import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts", indexes = {
        @Index(columnList = "member_id"),
        @Index(columnList = "title"),
        @Index(columnList = "created_at")
})
@Entity
public class Post extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean deleted = false;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Post)) return false;
        Post post = (Post) obj;
        return id != null && id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
