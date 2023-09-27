package com.leesh.devlab.jwt;

import com.leesh.devlab.constant.TokenType;

public interface AuthToken {

    TokenType getTokenType();

    String getValue();

    long getExpiredAt();

}
