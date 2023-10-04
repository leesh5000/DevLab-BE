package com.leesh.devlab.jwt;

import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.jwt.dto.LoginInfo;

public interface TokenService {

    LoginInfo extractLoginInfo(String value) throws AuthException;

    void validateToken(String value, TokenType tokenType) throws AuthException;

    Token createToken(LoginInfo loginInfo, TokenType tokenType);

}
