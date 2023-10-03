package com.leesh.devlab.jwt;

public interface AuthToken {

    TokenType getTokenType();

    String getValue();

    long getExpiredAt();

}
