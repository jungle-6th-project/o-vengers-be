package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.config.security.filter.token.TokenResolver;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.exception.RefreshTokenInvalidException;
import jungle.ovengers.model.dto.MemberDto;
import jungle.ovengers.model.oauth.KakaoTokenResponse;
import jungle.ovengers.model.oauth.KakaoUserInfoResponse;
import jungle.ovengers.model.request.AuthRequest;
import jungle.ovengers.model.response.MemberResponse;
import jungle.ovengers.model.response.Token;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.support.TokenGenerator;
import jungle.ovengers.support.converter.MemberConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenGenerator tokenGenerator;
    private final TokenResolver tokenResolver;
    private final AuditorHolder auditorHolder;

    private final String client_id = "997f10e0eac4d170ed7b30fa0c28d314";
    private final String kakaoUri = "https://kauth.kakao.com";
    private final String kakaoApiUri = "https://kapi.kakao.com";
    @Value("${kakao.redirect-uri}")
    private String redirect_uri;

    public MemberResponse getUserInfo() {
        Long memberId = auditorHolder.get();
        MemberEntity memberEntity = memberRepository.findById(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));
        MemberDto memberDto = MemberConverter.from(memberEntity);
        return new MemberResponse(memberDto.getName(), memberDto.getProfile(), memberDto.getEmail(), null);
    }

    public MemberResponse getUserInfoByGroup(Long groupId) {
        Long memberId = auditorHolder.get();
        MemberEntity memberEntity = memberRepository.findById(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));
        MemberDto memberDto = MemberConverter.from(memberEntity);
        /*
            Todo : 어떤 그룹에서 몇 시간 누적 공부했는지 조회해서 내려주어야함.
         */
        return new MemberResponse(memberDto.getName(), memberDto.getProfile(), memberDto.getEmail(), null);
    }

    public Token publishToken(AuthRequest authRequest) {
        KakaoTokenResponse kakaoTokenResponse = getKakaoTokenResponse(authRequest.getAuthCode());
        KakaoUserInfoResponse kakaoUserInfoResponse = getKakaoUserInfoResponse(kakaoTokenResponse);

        MemberDto memberDto = new MemberDto(kakaoUserInfoResponse.getKakaoAccount()
                                                                 .getProfile()
                                                                 .getNickname(),
                                            kakaoUserInfoResponse.getKakaoAccount()
                                                                 .getProfile()
                                                                 .getProfileImageUrl(),
                                            kakaoUserInfoResponse.getKakaoAccount()
                                                                 .getEmail());

        MemberEntity existMemberEntity = memberRepository.findByEmail(kakaoUserInfoResponse.getKakaoAccount()
                                                                                           .getEmail());
        if (existMemberEntity != null) {
            return tokenGenerator.generateToken(existMemberEntity.getId());
        }

        MemberEntity memberEntity = memberRepository.save(MemberConverter.to(memberDto));
        return tokenGenerator.generateToken(memberEntity.getId());
    }

    private KakaoUserInfoResponse getKakaoUserInfoResponse(KakaoTokenResponse kakaoTokenResponse) {
        WebClient webClient;
        webClient = WebClient.builder()
                             .baseUrl(kakaoApiUri)
                             .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                             .build();
        KakaoUserInfoResponse kakaoUserInfoResponse = webClient.post()
                                                               .uri(uriBuilder -> uriBuilder.path("/v2/user/me")
                                                                                            .build())
                                                               .header("Authorization", "Bearer " + kakaoTokenResponse.getAccessToken())
                                                               .retrieve()
                                                               .bodyToMono(KakaoUserInfoResponse.class)
                                                               .block();
        return kakaoUserInfoResponse;
    }

    private KakaoTokenResponse getKakaoTokenResponse(String authCode) {
        WebClient webClient = WebClient.builder()
                                       .baseUrl(kakaoUri)
                                       .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                       .build();
        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                                                     .queryParam("grant_type", "authorization_code")
                                                     .queryParam("client_id", client_id)
                                                     .queryParam("code", authCode)
                                                     .queryParam("redirect_uri", redirect_uri)
                                                     .build())
                        .retrieve()
                        .bodyToMono(KakaoTokenResponse.class)
                        .block();
    }

    public Token reissueTokens(String refreshToken) {
        Long memberId = tokenResolver.resolveToken(refreshToken)
                                     .orElseThrow(RefreshTokenInvalidException::new);

        if (memberRepository.existsById(memberId)) {
            throw new RefreshTokenInvalidException();
        }
        return tokenGenerator.generateToken(memberId);
    }
}
