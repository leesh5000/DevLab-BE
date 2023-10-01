package com.leesh.devlab.domain.post;

import com.leesh.devlab.constant.Category;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.domain.BaseEntity;
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
import java.util.stream.Collectors;

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

        // 현재 게시글의 해시태그 중에서 입력으로 새로 들어온 태그가 존재하지 않으면, 해당 해시태그를 삭제한다.
        // hashtags.clear(); 하고, newTags를 새로 넣으면, 태그가 같은 항목이라도 새로운 hashtag 테이블을 생성하게 되므로 위와 같은 방식을 사용한다.
        Set<Tag> untagTarget = hashtags.stream()
                .map(Hashtag::getTag)
                .collect(Collectors.toSet());

        untagging(untagTarget);

        // 입력으로 새로 들어온 태그 목록들을 태깅한다.
        for (Tag tag : newTags) {
            tagging(tag);
        }
    }

    private void untagging(Set<Tag> target) {

        Set<Hashtag> hashtagsCopy = new HashSet<>(this.getHashtags());
        for (Hashtag hashtag : hashtagsCopy) {
            Tag tag = hashtag.getTag();
            if (target.contains(tag)) {
                this.hashtags.remove(hashtag);
            }
        }
    }

    private void tagging(Tag tag) {

        // 한 게시글 당 태그는 10개 이하
        if (hashtags.size() == 10) {
            throw new BusinessException(ErrorCode.EXCEED_HASHTAG_COUNT, "maximum size");
        }

        // 이 게시글의 해시태그 목록에 이미 존재하는 태그이면, 넘긴다.
        if (this.getHashtags().stream().anyMatch(hashtag -> hashtag.getTag().equals(tag))) {
            return;
        }

        Hashtag newHashtag = new Hashtag(this, tag);
        this.getHashtags().add(newHashtag);
    }

    /* 비즈니스 로직 */
    public void edit(Long memberId, String title, String contents, Category category, List<Tag> newTags) {

        // 게시글의 작성자만 수정 가능
        if (!Objects.equals(this.member.getId(), memberId)) {
            throw new BusinessException(ErrorCode.NOT_POST_AUTHOR, "not post author");
        }

        this.title = title;
        this.contents = contents;
        this.category = category;
        this.tagging(newTags);

    }

}
