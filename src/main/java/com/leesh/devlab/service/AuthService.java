package com.leesh.devlab.service;

import com.leesh.devlab.api.auth.dto.LoginInfo;
import com.leesh.devlab.api.auth.dto.OauthLoginInfo;
import com.leesh.devlab.api.auth.dto.RegisterInfo;
import com.leesh.devlab.api.auth.dto.TokenRefreshDto;
import com.leesh.devlab.constant.ErrorCode;
import com.leesh.devlab.constant.GrantType;
import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.domain.member.Member;
import com.leesh.devlab.domain.member.MemberRepository;
import com.leesh.devlab.exception.custom.AuthException;
import com.leesh.devlab.exception.custom.BusinessException;
import com.leesh.devlab.external.OauthMemberInfo;
import com.leesh.devlab.external.OauthService;
import com.leesh.devlab.external.OauthServiceFactory;
import com.leesh.devlab.external.OauthToken;
import com.leesh.devlab.jwt.AuthToken;
import com.leesh.devlab.jwt.AuthTokenService;
import com.leesh.devlab.jwt.dto.MemberInfo;
import jakarta.servlet.http.HttpSession;
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

    /**
     * 소셜 로그인 API
     * @param request
     * @return
     */
    public OauthLoginInfo.Response oauthLogin(Request request) {

        // 외부 oauth provider 에서 사용자 정보를 가져온다.
        OauthMemberInfo oauthMember = getOauthMemberInfo(request);

        // Oauth Provider 에서 가져온 사용자 식별값을 이용하여 DB에서 가입된 회원을 찾는다. 만약 회원가입한 유저가 아니라면, 신규 가입을 하고 가져온다.
        String oauthId = oauthMember.getOauthId();
        Member findMember = memberRepository.findByOauthId(oauthId)
                .orElseGet(() -> {
                    Member entity = oauthMember.toEntity();
                    return memberRepository.save(entity);
                });

        // 인증 토큰을 생성한다.
        MemberInfo memberInfo = MemberInfo.from(findMember);
        AuthToken accessToken = authTokenService.createAuthToken(memberInfo, TokenType.ACCESS);
        AuthToken refreshToken = authTokenService.createAuthToken(memberInfo, TokenType.REFRESH);

        // 유저의 refresh token을 업데이트한다.
        findMember.updateRefreshToken(refreshToken);

        // 응답 DTO를 생성 후 반환한다.
        return new OauthLoginInfo.Response(GrantType.BEARER, accessToken, refreshToken);
    }

    /**
     * 유저의 리프레시 토큰을 사용하여 새로운 인증 토큰을 발급하는 메서드
     * @param refreshToken
     */
    public TokenRefreshDto refresh(String refreshToken) {

        // 리프레시 토큰 검증
        authTokenService.validateAuthToken(refreshToken, TokenType.REFRESH);

        // 검증을 통과했으면, 리프레시 토큰을 통해 유저를 찾는다.
        // 리프레시 토큰이 탈취 되었을 때, 블락 처리를 할 수 있어야 하기 때문에 DB에서 유저를 찾아야한다.
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN, "not invalid refresh token"));

        // 리프레시 토큰이 만료됐으면, 예외를 던진다.
        if (member.getRefreshToken().isExpired()) {
            throw new AuthException(ErrorCode.INVALID_TOKEN, "not invalid refresh token");
        }

        // 새로운 액세스 토큰을 발급한다.
        AuthToken accessToken = authTokenService.createAuthToken(MemberInfo.from(member), TokenType.ACCESS);

        return new TokenRefreshDto(GrantType.BEARER, accessToken);

    }

    private OauthMemberInfo getOauthMemberInfo(Request request) {

        // oauth 타입에 맞는 oauth api service 구현체를 가져온다.
        OauthService oauthService = oauthServiceFactory.getService(request.oauthType());

        // 현재 로그인을 시도한 유저 정보를 가져오기 위해 먼저 토큰을 발급받는다.
        OauthToken oauthToken = oauthService.requestToken(request.authorizationCode());

        // 토큰을 이용하여 유저 정보를 가져온다.
        return oauthService.requestMemberInfo(oauthToken.getAccessToken());

    }

    public void logout(MemberInfo memberInfo) {

            Member member = memberRepository.findById(memberInfo.id())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));

            // 사용자를 로그아웃 처리한다.
            member.logout();
    }

    public void register(RegisterInfo.Request request) {

        // 이미 가입된 유저인지 확인한다.
        if (memberRepository.existsByLoginIdOrNickname(request.loginId(), request.nickname())) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_MEMBER, "already registered member");
        }

        // 회원가입을 진행한다.
        Member newMember = Member.builder()
                .loginId(request.loginId())
                .nickname(request.nickname())
                .password(passwordEncoder.encode(request.password()))
                .build();
        memberRepository.save(newMember);
    }

    public LoginInfo.Response login(LoginInfo.Request request) {

        Member findMember = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));

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

    // TODO : 현재는 한글 문자열을 그대로 내보내주고 있습니다. 글로벌 서비스를 고려하면 문자열로 메일을 보내는 것보다는 HTML 템플릿을 만들고 다국어 처리하는 것이 좋은 선택이지만, 현재는 개발 초기단계이므로 빠르게 서비스를 런칭 후에 시장의 반응을 본 다음 결정할 예정입니다.
    public void findIdAndPassword(String email) {

        // 가입된 유저인지 확인한다.
        Member findMember = checkRegisteredMember(email);

        // 임시 비밀번호를 생성 후 변경한다.
        String tempPassword = createRandomNumber();
        findMember.changePassword(passwordEncoder.encode(tempPassword));

        // 변경된 비밀번호를 사용자에게 전달한다.
        String title = "[DevLab] 아이디/비밀번호 정보 안내";
        String content =
                "[아이디] " + findMember.getLoginId() + "\n" +
                "[닉네임] " + findMember.getNickname() + "\n" +
                "[임시 비밀번호] " + tempPassword + "\n\n" +
                "로그인 후 비밀번호를 변경해주세요.";

        mailService.sendMail(findMember.getEmail(), title, content);
    }

    private Member checkRegisteredMember(String email) throws BusinessException {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));
    }

    private String createRandomNumber() {
        return String.valueOf((int) (Math.random() * 899999) + 100000);
    }

    // TODO : 서비스 런칭 후, 스케일 아웃이 고려되면 Session 방식은 적절하지 않기 때문에 Redis와 같은 여러 서버가 공유할 수 있는 외부 저장소로 변경해야 합니다. 하지만, 아직은 개발 초기 단계이므로 우선은 빠른 개발을 목표로 하고 추후 시장의 반응에 따라 변경할 예정입니다.
    public void emailVerify(String email, HttpSession session) {

        // 인증번호를 생성하고, 세션에 저장한다.
        String randomNumber = createRandomNumber();
        session.setAttribute(email, randomNumber);
        session.setMaxInactiveInterval(60 * 3); // 3분

        // 이메일 인증을 진행한다.
        String title = "[DevLab] 이메일 인증번호 안내";
        String contents = "[이메일 인증번호] " + randomNumber + "\n" +
                "인증번호 유효시간은 3분 입니다.";

        mailService.sendMail(email, title, contents);
    }

    // TODO : 추후 스케일 아웃이 고려될 때 Redis와 같은 외부 저장소를 사용하여 인증 번호를 관리해야 합니다.
    public void emailConfirm(String email, String code, MemberInfo memberInfo, HttpSession session) {

        // 세션에 저장된 인증번호를 가져온다.
        String cert = (String) session.getAttribute(email);
        if (!code.equals(cert)) {
            throw new BusinessException(ErrorCode.WRONG_CERT_NUMBER, "wrong certification number");
        }

        // 인증번호가 일치하면, 토큰 정보를 통해서 유저를 조회한다.
        Member member = memberRepository.findById(memberInfo.id())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_MEMBER, "not exist member"));

        // 유저의 이메일 정보를 업데이트한다.
        member.verifyEmail(email);

    }
}
