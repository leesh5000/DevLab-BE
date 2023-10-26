package com.leesh.devlab.constant.dto;

import com.leesh.devlab.validation.LoginId;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(@LoginId String loginId, @Size(min = 4, max = 255) String password) {

}
