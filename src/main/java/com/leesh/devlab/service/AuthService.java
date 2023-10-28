package com.leesh.devlab.service;

import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.constant.OauthType;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.constant.dto.*;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthAttributes;
import com.leesh.devlab.external.OauthService;
import com.leesh.devlab.external.OauthServiceFactory;
import com.leesh.devlab.external.OauthToken;
import com.leesh.devlab.jwt.Token;
import com.leesh.devlab.jwt.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

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
    public LoginResponseDto oauthLogin(OauthLoginRequestDto requestDto) {

        // 외부 oauth provider 에서 사용자 정보를 가져온다.
        OauthAttributes oauthAttributes = getOauthAttributes(requestDto);

        // Oauth Provider 에서 가져온 사용자 식별값을 이용하여 DB에서 가입된 회원을 찾는다. 만약 회원가입한 유저가 아니라면, 신규 가입을 하고 가져온다.
        Member findMember = memberService.getOrSaveByOauthId(oauthAttributes);

        // 인증 토큰을 생성한다.
        TokenInfoDto tokenInfoDto = generateResponseToken(findMember);
        UserInfoDto userInfoDto = new UserInfoDto(findMember.getId(), findMember.getNickname(), findMember.getRole());
        return new LoginResponseDto(tokenInfoDto, userInfoDto);
    }

    @Transactional
    public RegisterResponseDto register(RegisterRequestDto requestDto) {

        memberService.checkExistMember(requestDto.loginId(), requestDto.nickname());

        Member newMember = Member.createMember(requestDto.loginId(), requestDto.nickname(), passwordEncoder.encode(requestDto.password()));

        // 이메일 인증된 회원이면, 해당 이메일로 보안코드를 전송한다.
        if (requestDto.email().verified()) {
            String securityCode = newMember.verify();
            mailService.sendMail(requestDto.email().address(), "[DevLab] 계정 보안코드 안내", newMember.getLoginId() + "의 계정 보안코드 : " + securityCode);
        }

        Long id = memberRepository.save(newMember).getId();
        return new RegisterResponseDto(id);
    }

    @Transactional
    public LoginResponseDto login(@Valid LoginRequestDto request) {

        Member findMember = memberService.getByLoginId(request.loginId());

        if (!passwordEncoder.matches(request.password(), findMember.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD, "wrong password");
        }

        TokenInfoDto tokenInfoDto = generateResponseToken(findMember);
        UserInfoDto userInfoDto = new UserInfoDto(findMember.getId(), findMember.getNickname(), findMember.getRole());
        return new LoginResponseDto(tokenInfoDto, userInfoDto);
    }

    private TokenInfoDto generateResponseToken(Member findMember) {
        LoginMemberDto loginMemberDto = LoginMemberDto.from(findMember);
        Token accessToken = tokenService.createToken(loginMemberDto, TokenType.ACCESS);
        Token refreshToken = tokenService.createToken(loginMemberDto, TokenType.REFRESH);

        // 유저의 refresh token을 업데이트한다.
        findMember.updateRefreshToken(refreshToken);

        return new TokenInfoDto(GrantType.BEARER.getType(), accessToken, refreshToken);
    }

    public LoginResponseDto refreshToken(String refreshToken) {

        // 리프레시 토큰 검증
        tokenService.validateToken(refreshToken, TokenType.REFRESH);

        // 검증을 통과했으면, 리프레시 토큰을 통해 유저를 찾는다.
        // 리프레시 토큰이 탈취 되었을 때, 블락 처리를 할 수 있어야 하기 때문에 DB에서 유저를 찾아야한다.
        Member findMember = memberService.getByRefreshToken(refreshToken);

        // 리프레시 토큰이 만료됐으면, 예외를 던진다.
        if (findMember.getRefreshToken().isExpired()) {
            throw new AuthException(ErrorCode.EXPIRED_REFRESH_TOKEN, "expired refresh token");
        }

        // 새로운 액세스 토큰을 발급한다.
        Token aceessToken = tokenService.createToken(LoginMemberDto.from(findMember), TokenType.ACCESS);
        TokenInfoDto tokenInfoDto = new TokenInfoDto(GrantType.BEARER.getType(), aceessToken, null);
        UserInfoDto userInfoDto = new UserInfoDto(findMember.getId(), findMember.getNickname(), findMember.getRole());
        return new LoginResponseDto(tokenInfoDto, userInfoDto);

    }

    private OauthAttributes getOauthAttributes(OauthLoginRequestDto requestDto) {

        // oauth 타입에 맞는 oauth api service 구현체를 가져온다.
        OauthService oauthService = oauthServiceFactory.getService(requestDto.oauthType());

        // 현재 로그인을 시도한 유저 정보를 가져오기 위해 먼저 토큰을 발급받는다.
        OauthToken oauthToken = oauthService.fetchToken(requestDto.authorizationCode());

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

    public FindLoginIdResponseDto findLoginId(FindLoginIdRequestDto requestDto) {

        Member member = getMemberBySecurityCode(requestDto.securityCode());
        OauthType oauthType = (member.getOauth() != null) ? member.getOauth().getType() : null;

        return new FindLoginIdResponseDto(member.getLoginId(), oauthType);
    }

    private Member getMemberBySecurityCode(String securityCode) {
        return memberRepository.findBySecurityCode(securityCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member by security code = " + securityCode));
    }

    public void checkSecurityCode(CheckSecurityCodeRequestDto requestDto) {

        Member member = memberRepository.findByLoginId(requestDto.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member by login id = " + requestDto.loginId()));

        if (!Objects.equals(member.getSecurityCode(), requestDto.securityCode())) {
            throw new BusinessException(ErrorCode.WRONG_SECURITY_CODE, "wrong security code");
        }
    }
}
