package com.leesh.devlab.api.oauth;

import com.leesh.devlab.api.oauth.dto.OauthLogin;
import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.external.OauthClient;
import com.leesh.devlab.external.OauthClientFactory;
import com.leesh.devlab.external.abstraction.dto.OauthMemberInfo;
import com.leesh.devlab.external.abstraction.dto.OauthTokenResponse;
import com.leesh.devlab.jwt.AuthToken;
import com.leesh.devlab.jwt.AuthTokenService;
import com.leesh.devlab.jwt.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.leesh.devlab.api.oauth.dto.OauthLogin.Request;
import static com.leesh.devlab.util.TimeUtils.convertLocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class OauthService {

    private final OauthClientFactory oauthClientFactory;
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;

    public OauthLogin.Response oauthLogin(Request request) {

        // 외부 oauth provider 에서 사용자 정보를 가져온다.
        OauthMemberInfo oauthMemberInfo = getOauthMemberInfo(request);

        // Oauth Provider 에서 가져온 사용자 정보를 이용하여 DB에서 가입된 회원을 찾는다. 만약 회원가입한 유저가 아니라면, 신규 가입을 하고 가져온다.
        Member findMember = memberRepository.findByEmail(oauthMemberInfo.getEmail())
                .orElseGet(() -> {
                    Member m = Member.createMember(oauthMemberInfo.getName(), oauthMemberInfo.getEmail(), oauthMemberInfo.getOauthType());
                    return memberRepository.save(m);
                });

        // 소셜 계정으로 로그인 시도한 사용자의 유효성을 검증한다.
        validateOauthMember(findMember, oauthMemberInfo);

        // 인증 토큰을 생성한다.
        MemberInfo memberInfo = MemberInfo.from(findMember);
        AuthToken accessToken = authTokenService.createAuthToken(memberInfo, TokenType.ACCESS);
        AuthToken refreshToken = authTokenService.createAuthToken(memberInfo, TokenType.REFRESH);

        // 유저의 refresh token을 업데이트한다.
        findMember.updateRefreshToken(
                refreshToken,
                convertLocalDateTime(
                        new Date(System.currentTimeMillis() + TokenType.REFRESH.getExpiresIn())
                )
        );

        // 응답 DTO를 생성 후 반환한다.
        return new OauthLogin.Response(GrantType.BEARER, accessToken, refreshToken);
    }

    /**
     * 소셜 계정으로 로그인 시도한 사용자의 유효성 검증 메서드
     * @param findMember
     * @param oauthMemberInfo
     */
    private static void validateOauthMember(Member findMember, OauthMemberInfo oauthMemberInfo) {

        // 찾은 유저가 탈퇴한 상태라면 재가입 로직을 진행한다.
        if (findMember.isDeleted()) {
            findMember.reRegister(oauthMemberInfo);
        }

        // 소셜 계정으로 로그인을 시도한 유저가 올바른 소셜 계정으로 로그인을 한 것인지 체크한다.
        findMember.validateOauthType(oauthMemberInfo.getOauthType());

    }

    private OauthMemberInfo getOauthMemberInfo(Request request) {

        // oauth 타입에 맞는 oauth api service 구현체를 가져온다.
        OauthClient oauthClient = oauthClientFactory.getService(request.oauthType());

        // 현재 로그인을 시도한 유저 정보를 가져오기 위해 먼저 토큰을 발급받는다.
        OauthTokenResponse oauthTokenResponse = oauthClient.requestToken(request.authorizationCode());

        // 토큰을 이용하여 유저 정보를 가져온다.
        return oauthClient.requestMemberInfo(oauthTokenResponse.getAccessToken());

    }
}
