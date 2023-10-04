package com.leesh.devlab.dto;

import lombok.Builder;

@Builder
public record Activities(int postCount, int postLikeCount, int commentCount, int commentLikeCount) {

}