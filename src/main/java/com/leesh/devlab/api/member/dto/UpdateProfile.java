package com.leesh.devlab.api.member.dto;

import com.leesh.devlab.validator.Nickname;

public record UpdateProfile(@Nickname String nickname) {

}
