package com.leesh.devlab.constant;

import com.leesh.devlab.exception.ex.BusinessException;

import static com.leesh.devlab.constant.ErrorCode.ALREADY_REGISTERED_FROM_GOOGLE;

/**
 * 유저의 회원 유형
 * <ul>
 *     <li>KAKAO: 카카오 소셜 계정으로 가입한 회원</li>
 *     <li>NAVER: 네이버 소셜 계정으로 가입한 회원</li>
 *     <li>GOOGLE: 구글 소셜 계정으로 가입한 회원</li>
 * <ul>
 */
public enum OauthType {

    KAKAO, NAVER, GOOGLE;

    public static void validateOauthType(OauthType userOauthType, OauthType requestOauthType) {
        if (userOauthType != requestOauthType) {
            switch (userOauthType) {
                case GOOGLE -> throw new BusinessException(ALREADY_REGISTERED_FROM_GOOGLE, "already registered from google");
                case NAVER -> throw new BusinessException(ErrorCode.ALREADY_REGISTERED_FROM_NAVER, "already registered from naver");
                case KAKAO -> throw new BusinessException(ErrorCode.ALREADY_REGISTERED_FROM_KAKAO, "already registered from kakao");
            }
        }
    }

}
