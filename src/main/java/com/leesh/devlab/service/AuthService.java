package com.leesh.devlab.service;

import com.leesh.devlab.api.auth.dto.*;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthMemberInfo;
import com.leesh.devlab.external.OauthService;
import com.leesh.devlab.external.OauthServiceFactory;
import com.leesh.devlab.external.OauthToken;
import com.leesh.devlab.jwt.AuthToken;
import com.leesh.devlab.jwt.AuthTokenService;
import com.leesh.devlab.jwt.GrantType;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.jwt.dto.MemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leesh.devlab.api.auth.dto.OauthLoginInfo.Request;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

    private final OauthServiceFactory oauthServiceFactory;
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final MemberService memberService;

    public OauthLoginInfo.Response oauthLogin(Request request) {

        // 외부 oauth provider 에서 사용자 정보를 가져온다.
        OauthMemberInfo oauthMemberInfo = getOauthMemberInfo(request);

        // Oauth Provider 에서 가져온 사용자 식별값을 이용하여 DB에서 가입된 회원을 찾는다. 만약 회원가입한 유저가 아니라면, 신규 가입을 하고 가져온다.
        String oauthId = oauthMemberInfo.getOauthId();
        Member findMember = memberService.getOrSaveByOauthId(oauthId, oauthMemberInfo);

        // 인증 토큰을 생성한다.
        MemberInfo memberInfo = MemberInfo.from(findMember);
        AuthToken accessToken = authTokenService.createAuthToken(memberInfo, TokenType.ACCESS);
        AuthToken refreshToken = authTokenService.createAuthToken(memberInfo, TokenType.REFRESH);

        // 유저의 refresh token을 업데이트한다.
        findMember.updateRefreshToken(refreshToken);

        // 응답 DTO를 생성 후 반환한다.
        return new OauthLoginInfo.Response(GrantType.BEARER, accessToken, refreshToken);
    }

    public RefreshTokenInfo refresh(String refreshToken) {

        // 리프레시 토큰 검증
        authTokenService.validateAuthToken(refreshToken, TokenType.REFRESH);

        // 검증을 통과했으면, 리프레시 토큰을 통해 유저를 찾는다.
        // 리프레시 토큰이 탈취 되었을 때, 블락 처리를 할 수 있어야 하기 때문에 DB에서 유저를 찾아야한다.
        Member member = memberService.getByRefreshToken(refreshToken);

        // 리프레시 토큰이 만료됐으면, 예외를 던진다.
        if (member.getRefreshToken().isExpired()) {
            throw new AuthException(ErrorCode.INVALID_TOKEN, "not invalid refresh token");
        }

        // 새로운 액세스 토큰을 발급한다.
        AuthToken accessToken = authTokenService.createAuthToken(MemberInfo.from(member), TokenType.ACCESS);

        return new RefreshTokenInfo(GrantType.BEARER, accessToken);

    }

    private OauthMemberInfo getOauthMemberInfo(Request request) {

        // oauth 타입에 맞는 oauth api service 구현체를 가져온다.
        OauthService oauthService = oauthServiceFactory.getService(request.oauthType());

        // 현재 로그인을 시도한 유저 정보를 가져오기 위해 먼저 토큰을 발급받는다.
        OauthToken oauthToken = oauthService.requestToken(request.authorizationCode());

        // 토큰을 이용하여 유저 정보를 가져온다.
        return oauthService.requestMemberInfo(oauthToken.getAccessToken());

    }

    public RegisterInfo.Response register(RegisterInfo.Request request) {

        // 이미 가입된 유저인지 확인한다.
        memberService.checkExistMember(request.loginId(), request.nickname());

        // 회원가입을 진행한다.
        Member newMember = Member.builder()
                .loginId(request.loginId())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .build();

        Long id = memberRepository.save(newMember).getId();

        return new RegisterInfo.Response(id);
    }

    public LoginInfo.Response login(LoginInfo.Request request) {

        Member findMember = memberService.getByLoginId(request);

        // 비밀번호가 일치하는지 확인한다.
        if (!passwordEncoder.matches(request.password(), findMember.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD, "wrong password");
        }

        // 토큰을 생성한다.
        MemberInfo memberInfo = MemberInfo.from(findMember);
        AuthToken accessToken = authTokenService.createAuthToken(memberInfo, TokenType.ACCESS);
        AuthToken refreshToken = authTokenService.createAuthToken(memberInfo, TokenType.REFRESH);

        // 유저의 refresh token을 업데이트한다.
        findMember.updateRefreshToken(refreshToken);

        return new LoginInfo.Response(GrantType.BEARER, accessToken, refreshToken);
    }

    // TODO : 추후 HTML 템플릿 처리 할 것
    public void findAccount(FindAccount requestDto) {

        Member findMember = memberService.getByEmail(requestDto.email());

        String title = "[DevLab] 아이디/비밀번호 정보 안내";
        String tempPassword = generateRandom6Digits();

        String content =
                "[아이디] " + findMember.getLoginId() + "\n" +
                        "[닉네임] " + findMember.getNickname() + "\n" +
                        "[임시 비밀번호] " + tempPassword + "\n\n" +
                        "로그인 후 비밀번호를 변경해주세요.";

        mailService.sendMail(findMember.getEmail(), title, content);

        findMember.changePassword(passwordEncoder.encode(tempPassword));

    }

    private String generateRandom6Digits() {
        return String.valueOf((int) (Math.random() * 899999) + 100000);
    }

}
