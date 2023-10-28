package com.leesh.devlab.constant.dto;

import com.querydsl.core.annotations.QueryProjection;

public record AuthorDto(Long id, String nickname) {

    @QueryProjection
    public AuthorDto {
    }
}
