package com.leesh.devlab.dto;

import com.leesh.devlab.validation.Email;

public record EmailVerify(@Email String email) {
}
