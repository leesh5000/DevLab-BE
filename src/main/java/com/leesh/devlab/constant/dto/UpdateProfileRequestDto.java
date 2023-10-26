package com.leesh.devlab.constant.dto;

import com.leesh.devlab.validation.Nickname;

public record UpdateProfileRequestDto(@Nickname String nickname,
                                      EmailDto email,
                                      String introduce) {

}
