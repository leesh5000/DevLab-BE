package com.leesh.devlab.api.member.dto;

import com.leesh.devlab.validation.Email;

public record EmailVerify(@Email String email) {
}
