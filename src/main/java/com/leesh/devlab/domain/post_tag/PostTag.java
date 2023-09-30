package com.leesh.devlab.domain.post_tag;

import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.tag.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts_tags", indexes = {
        @Index(columnList = "post_id"),
        @Index(columnList = "tag_id"),
})
@Entity
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Post post;

    @JoinColumn(name = "tag_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Tag tag;

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
        post.getPostTags().add(this);
        tag.getPostTags().add(this);
    }
}
