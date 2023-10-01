package com.leesh.devlab.domain.post;

import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post_tag.PostTag;
import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.exception.custom.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    /* 비즈니스 로직 */
    public void tagging(Set<Tag> tags) {

        // 10개 이상 태그 불가능
        if (tags.size() > 10) {
            throw new BusinessException(ErrorCode.EXCEED_TAG_COUNT, "tag count must be less than 10");
        }

        for (Tag tag : tags) {
            PostTag postTag = PostTag.of(this, tag);
            this.postTags.add(postTag);
        }

        // tag.getPostTags()를 하는 순간 Lazy Loading이 되는데, 게시글에 매번 태그를 추가할 때마다 태그에 딸린 게시글들을 가져오는 것은
        // 자원낭비이고, 게시글에 태그를 붙일떄 태그에서 PostTag를 가져오는 일이 없으므로 굳이 연관관계를 맺을 필요가 없다.
        // tag.getPostTags().add(postTag);
    }

    public void edit(Long memberId, String title, String contents, Category category, Set<Tag> tags) {

        if (!Objects.equals(this.member.getId(), memberId)) {
            throw new BusinessException(ErrorCode.NOT_POST_AUTHOR, "not post author");
        }

        this.title = title;
        this.contents = contents;
        this.category = category;

        // 기존 태그를 모두 삭제한다.
        this.postTags.clear();

        // 새로운 태그를 추가한다.
        this.tagging(tags);

    }
}
