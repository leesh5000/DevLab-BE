package com.leesh.devlab.constant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.constant.Category;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.validation.Contents;
import com.leesh.devlab.validation.Tags;

import java.util.Set;

public record CreatePostRequestDto(String title, @Contents String contents, Category category, @Tags @JsonProperty("tags") Set<String> tagNames) {

    public Post toEntity(Member member) {

        return Post.builder()
                .title(title)
                .contents(contents)
                .category(category)
                .member(member)
                .build();
    }
}
