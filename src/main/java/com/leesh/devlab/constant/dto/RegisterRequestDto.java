package com.leesh.devlab.constant.dto;

import com.leesh.devlab.validation.LoginId;
import com.leesh.devlab.validation.Nickname;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(@LoginId String loginId, @Size(min = 4, max = 255) String password, @Nickname String nickname, EmailDto email) {
}
