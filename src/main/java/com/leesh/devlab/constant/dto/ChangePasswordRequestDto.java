package com.leesh.devlab.constant.dto;

public record ChangePasswordRequestDto(String loginId, String securityCode, String password) {

}
