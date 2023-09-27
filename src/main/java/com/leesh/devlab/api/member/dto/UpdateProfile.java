package com.leesh.devlab.api.member.dto;

import com.leesh.devlab.validator.Email;
import com.leesh.devlab.validator.Nickname;
import com.leesh.devlab.validator.Password;

public record UpdateProfile(@Nickname String nickname,
                            @Password String password,
                            @Email String email) {

}
