package com.leesh.devlab.api.oauth.dto;

import jakarta.validation.constraints.Email;

public class FindDto {

    private FindDto() {
    }

    public record Request(@Email String email) {
    }

}
