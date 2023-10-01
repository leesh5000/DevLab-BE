package com.leesh.devlab.api.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.leesh.devlab.constant.Category;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.validator.Tags;

import java.util.Set;

public class PostDto {

    private PostDto() {
    }

    public record Request(String title, String contents, Category category, @Tags @JsonProperty("tags") Set<String> tagNames) {

        public Post toEntity(Member member) {

            return Post.builder()
                    .title(title)
                    .contents(contents)
                    .category(category)
                    .member(member)
                    .build();
        }
    }

    public record Response(Long postId) {

        public static Response from(Long id) {
            return new Response(id);
        }
    }
}
