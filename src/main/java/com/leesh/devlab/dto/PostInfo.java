package com.leesh.devlab.dto;

import com.leesh.devlab.domain.post.Category;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PostInfo {
        private final Long id;
        private final String title;
        private final String contents;
        private final Category category;
        private final Long createdAt;
        private final Long modifiedAt;
        private final String author;
        private final long commentCount;
        private final long likeCount;
        private final Set<String> tags = new HashSet<>();

    @QueryProjection
    public PostInfo(Long id, String title, String contents, Category category, Long createdAt, Long modifiedAt, String author, long commentCount, long likeCount) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.category = category;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.author = author;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
    }

    public void addTags(String tag) {
        this.tags.add(tag);
    }

    public void addTags(Set<String> tags) {
        this.tags.addAll(tags);
    }
}
