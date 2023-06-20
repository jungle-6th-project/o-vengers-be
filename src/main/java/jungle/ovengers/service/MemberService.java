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
import jungle.ovengers.support.TokenGenerator;
import jungle.ovengers.support.converter.MemberConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenGenerator tokenGenerator;
    private final TokenResolver tokenResolver;
    private final AuditorHolder auditorHolder;

    private final String clientId = "997f10e0eac4d170ed7b30fa0c28d314";
    private final String kakaoUri = "https://kauth.kakao.com";
    private final String kakaoApiUri = "https://kapi.kakao.com";

    @Value("${kakao.adminKey}")
    private String adminKey;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

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


        MemberEntity existMemberEntity = memberRepository.findByEmailAndDeletedFalse(kakaoUserInfoResponse.getKakaoAccount()
                                                                                                          .getEmail())
                                                         .orElse(null);

        if (existMemberEntity != null) {
            return tokenGenerator.generateToken(existMemberEntity.getId());
        }

        MemberEntity memberEntity = memberRepository.save(MemberConverter.to(kakaoUserInfoResponse));
        return tokenGenerator.generateToken(memberEntity.getId());
    }

    private KakaoUserInfoResponse getKakaoUserInfoResponse(KakaoTokenResponse kakaoTokenResponse) {
        WebClient webClient;
        webClient = WebClient.builder()
                             .baseUrl(kakaoApiUri)
                             .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                             .build();
        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/v2/user/me")
                                                     .build())
                        .header("Authorization", "Bearer " + kakaoTokenResponse.getAccessToken())
                        .retrieve()
                        .bodyToMono(KakaoUserInfoResponse.class)
                        .block();
    }

    private KakaoTokenResponse getKakaoTokenResponse(String authCode) {
        WebClient webClient = WebClient.builder()
                                       .baseUrl(kakaoUri)
                                       .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                       .build();
        return webClient.post()
                        .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                                                     .queryParam("grant_type", "authorization_code")
                                                     .queryParam("client_id", clientId)
                                                     .queryParam("code", authCode)
                                                     .queryParam("redirect_uri", redirectUri)
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

    public void logout() {
        Long memberId = auditorHolder.get();
        MemberEntity memberEntity = memberRepository.findByIdAndDeletedFalse(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));
        memberEntity.delete();
        WebClient webClient = WebClient.builder()
                                       .baseUrl(kakaoUri)
                                       .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                       .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + adminKey)
                                       .build();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("target_id_type", "user_id");
        requestBody.put("target_id", memberEntity.getCertificationId());
        webClient.post()
                 .uri(uriBuilder -> uriBuilder.path("/v1/user/logout")
                                              .build())
                 .body(BodyInserters.fromValue(requestBody))
                 .retrieve()
                 .toBodilessEntity()
                 .block();
        log.info("사용자 탈퇴 성공");
    }
}
