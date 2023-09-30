package com.leesh.devlab.domain.post;

import com.leesh.devlab.constant.Category;
import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post_tag.PostTag;
import com.leesh.devlab.domain.tag.Tag;
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
@Table(name = "posts", indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "contents"),
        @Index(columnList = "category"),
        @Index(columnList = "member_id"),
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

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 10, nullable = false)
    private Category category;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Member member;

    @OrderBy("id")
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<PostTag> postTags = new ArrayList<>();

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

    /* 생성 메서드 */
    @Builder
    private Post(String title, String contents, Category category, Member member) {
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.member = member;
    }

    public PostTag tagging(Tag tag) {
        return new PostTag(this, tag);
    }
}
