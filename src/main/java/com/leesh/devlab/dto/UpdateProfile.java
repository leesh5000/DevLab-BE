package com.leesh.devlab.dto;

import com.leesh.devlab.validation.Email;
import com.leesh.devlab.validation.Nickname;
import com.leesh.devlab.validation.Password;

public record UpdateProfile(@Nickname String nickname,
                            @Password String password,
                            @Email String email) {

}
