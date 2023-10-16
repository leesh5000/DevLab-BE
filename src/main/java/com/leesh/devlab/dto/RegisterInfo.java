package com.leesh.devlab.dto;

import com.leesh.devlab.validation.LoginId;
import com.leesh.devlab.validation.Nickname;
import jakarta.validation.constraints.Size;

public class RegisterInfo {

    protected RegisterInfo() {
    }

    public record Request(@LoginId String loginId, @Size(min = 4, max = 255) String password, @Nickname String nickname, boolean verified) {

    }

    public record Response(Long memberId) {

    }

}
