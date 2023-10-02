package com.leesh.devlab.domain.post;

import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.domain.BaseEntity;
import com.leesh.devlab.domain.comment.Comment;
import com.leesh.devlab.domain.hashtag.Hashtag;
import com.leesh.devlab.domain.like.Like;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.tag.Tag;
import com.leesh.devlab.exception.custom.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

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
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private final List<Comment> comments = new ArrayList<>();

    @OrderBy("id")
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Hashtag> hashtags = new ArrayList<>();

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

    /* 연관관계 메서드 */
    public void tagging(List<Tag> newTags) {

        // 한 게시글 당 태그는 10개 이하
        if (newTags.size() > 10) {
            throw new BusinessException(ErrorCode.EXCEED_HASHTAG_COUNT, "maximum size");
        }

        // 기존 해시태그들 중에서 입력으로 새로 들어온 태그가 없으면 삭제한다.
        Set<Hashtag> defaultHashtags = new HashSet<>(this.getHashtags());
        for (Hashtag hashtag : defaultHashtags) {
            Tag tag = hashtag.getTag();
            if (!newTags.contains(tag)) {
                this.hashtags.remove(hashtag);
            }
        }

        // 입력으로 새로 들어온 태그 목록들을 태깅한다.
        newTags.forEach(this::tagging);
    }

    private void tagging(Tag newTag) {

        // 한 게시글 당 태그는 10개 이하
        if (hashtags.size() == 10) {
            throw new BusinessException(ErrorCode.EXCEED_HASHTAG_COUNT, "maximum size");
        }

        // 이 게시글의 해시태그 목록에 이미 존재하는 태그이면, 넘긴다.
        if (hashtags.stream().anyMatch(hashtag -> hashtag.getTag().equals(newTag))) {
            return;
        }

        Hashtag newHashtag = new Hashtag(this, newTag);
        this.getHashtags().add(newHashtag);
    }

    /* 비즈니스 로직 */
    public void edit(String title, String contents, Category category, List<Tag> newTags) {

        this.title = title;
        this.contents = contents;
        this.category = category;
        this.tagging(newTags);

    }

}
