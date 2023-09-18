package com.leesh.devlab.domain.member.constant;

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

    public static void isValidOauthType(OauthType userOauthType, OauthType requestOauthType) {
        if (userOauthType != requestOauthType) {
            switch (userOauthType) {
                case GOOGLE -> throw new IllegalArgumentException("이미 구글 소셜 계정으로 가입한 회원입니다.");
                case NAVER -> throw new IllegalArgumentException("이미 네이버 소셜 계정으로 가입한 회원입니다.");
                case KAKAO -> throw new IllegalArgumentException("이미 카카오 소셜 계정으로 가입한 회원입니다.");
            }
        }
    }

}
