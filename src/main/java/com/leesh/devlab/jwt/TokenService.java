package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.constant.dto.LoginMemberDto;

public interface TokenService {

    LoginMemberDto extractLoginInfo(String value) throws AuthException;

    void validateToken(String value, TokenType tokenType) throws AuthException;

    Token createToken(LoginMemberDto loginMemberDto, TokenType tokenType);

}
