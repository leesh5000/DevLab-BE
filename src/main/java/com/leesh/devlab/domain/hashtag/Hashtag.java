package com.leesh.devlab.domain.hashtag;

import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.domain.tag.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hashtags", indexes = {
        @Index(columnList = "post_id"),
        @Index(columnList = "tag_id"),
})
@Entity
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "tag_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Tag tag;

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Hashtag hashtag)) return false;
        return id != null && id.equals(hashtag.id);
    }

    public Hashtag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }

}
