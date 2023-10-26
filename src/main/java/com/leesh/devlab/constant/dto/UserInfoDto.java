package com.leesh.devlab.constant.dto;

import com.leesh.devlab.constant.Role;

public record UserInfoDto(Long id, String nickname, Role role) {
}
