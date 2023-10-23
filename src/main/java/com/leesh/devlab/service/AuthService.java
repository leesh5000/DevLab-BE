package com.leesh.devlab.service;

import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.dto.Login;
import com.leesh.devlab.dto.RegisterInfo;
import com.leesh.devlab.dto.TokenRefreshInfo;
import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthAttributes;
import com.leesh.devlab.external.OauthService;
import com.leesh.devlab.external.OauthServiceFactory;
import com.leesh.devlab.external.OauthToken;
import com.leesh.devlab.jwt.GrantType;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenService;
import com.leesh.devlab.jwt.TokenType;
import com.leesh.devlab.jwt.dto.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leesh.devlab.dto.OauthLogin.Request;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final OauthServiceFactory oauthServiceFactory;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final MailService mailService;

    @Transactional
    public Login.Response oauthLogin(Request request) {

        // 외부 oauth provider 에서 사용자 정보를 가져온다.
        OauthAttributes oauthAttributes = getOauthAttributes(request);

        // Oauth Provider 에서 가져온 사용자 식별값을 이용하여 DB에서 가입된 회원을 찾는다. 만약 회원가입한 유저가 아니라면, 신규 가입을 하고 가져온다.
        Member findMember = memberService.getOrSaveByOauthId(oauthAttributes);

        // 인증 토큰을 생성한다.
        return generateResponseToken(findMember);
    }

    @Transactional
    public RegisterInfo.Response register(RegisterInfo.Request request) {

        memberService.checkExistMember(request.loginId(), request.nickname());

        Member newMember = Member.createMember(request.loginId(), request.nickname(), passwordEncoder.encode(request.password()));

        // 이메일 인증된 회원이면, 해당 이메일로 보안코드를 전송한다.
        if (request.email().verified()) {
            String securityCode = newMember.verify();
            mailService.sendMail(request.email().address(), "[DevLab] 계정 보안코드 안내", "계정 보안코드 : " + securityCode);
        }

        Long id = memberRepository.save(newMember).getId();
        return new RegisterInfo.Response(id);
    }

    @Transactional
    public Login.Response login(Login.Request request) {

        Member findMember = memberService.getByLoginId(request);

        if (!passwordEncoder.matches(request.password(), findMember.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD, "wrong password");
        }

        return generateResponseToken(findMember);
    }

    private Login.Response generateResponseToken(Member findMember) {
        LoginInfo loginInfo = LoginInfo.from(findMember);
        Token accessToken = tokenService.createToken(loginInfo, TokenType.ACCESS);
        Token refreshToken = tokenService.createToken(loginInfo, TokenType.REFRESH);

        // 유저의 refresh token을 업데이트한다.
        findMember.updateRefreshToken(refreshToken);

        return new Login.Response(GrantType.BEARER.getType(), accessToken, refreshToken);
    }

    public TokenRefreshInfo refreshToken(String refreshToken) {

        // 리프레시 토큰 검증
        tokenService.validateToken(refreshToken, TokenType.REFRESH);

        // 검증을 통과했으면, 리프레시 토큰을 통해 유저를 찾는다.
        // 리프레시 토큰이 탈취 되었을 때, 블락 처리를 할 수 있어야 하기 때문에 DB에서 유저를 찾아야한다.
        Member member = memberService.getByRefreshToken(refreshToken);

        // 리프레시 토큰이 만료됐으면, 예외를 던진다.
        if (member.getRefreshToken().isExpired()) {
            throw new AuthException(ErrorCode.EXPIRED_REFRESH_TOKEN, "expired refresh token");
        }

        // 새로운 액세스 토큰을 발급한다.
        Token accessToken = tokenService.createToken(LoginInfo.from(member), TokenType.ACCESS);

        return TokenRefreshInfo.of(GrantType.BEARER.getType(), accessToken, member.getId(), member.getLoginId(), member.getNickname());

    }

    private OauthAttributes getOauthAttributes(Request request) {

        // oauth 타입에 맞는 oauth api service 구현체를 가져온다.
        OauthService oauthService = oauthServiceFactory.getService(request.oauthType());

        // 현재 로그인을 시도한 유저 정보를 가져오기 위해 먼저 토큰을 발급받는다.
        OauthToken oauthToken = oauthService.fetchToken(request.authorizationCode());

        // 토큰을 이용하여 유저 정보를 가져온다.
        return oauthService.fetchAttributes(oauthToken.getAccessToken());

    }

    private String generateRandom6Digits() {
        return String.valueOf((int) (Math.random() * 899999) + 100000);
    }

    public void logout(String refreshToken) {

        memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member by refresh token = " + refreshToken))
                .logout();

    }

    public String generateVerificationCode() {
        return generateRandom6Digits();
    }
}
