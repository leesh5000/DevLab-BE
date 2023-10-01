package com.leesh.devlab.api.auth.dto;

import com.leesh.devlab.validator.LoginId;
import com.leesh.devlab.validator.Nickname;
import jakarta.validation.constraints.Size;

public class RegisterDto {

    protected RegisterDto() {
    }

    public record Request(@LoginId String loginId, @Size(min = 4, max = 255) String password, @Nickname String nickname) {

    }

    public record Response(Long memberId) {

    }

}
