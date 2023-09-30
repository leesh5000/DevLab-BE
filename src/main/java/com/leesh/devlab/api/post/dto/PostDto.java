package com.leesh.devlab.api.post.dto;

import com.leesh.devlab.constant.Category;
import com.leesh.devlab.domain.post.Post;
import com.leesh.devlab.validator.Tags;

import java.util.Set;

public class PostDto {

    private PostDto() {
    }

    public record Request(String title, String contents, Category category, @Tags Set<String> tags) {

    }

    public record Response(Long postId) {

        public static Response from(Post post) {
            return new Response(post.getId());
        }
    }
}
