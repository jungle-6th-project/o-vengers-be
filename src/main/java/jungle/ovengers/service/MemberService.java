package jungle.ovengers.service;

import jungle.ovengers.support.TokenGenerator;
import jungle.ovengers.support.converter.MemberConverter;
import jungle.ovengers.dto.MemberDto;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.model.oauth.KakaoTokenResponse;
import jungle.ovengers.model.oauth.KakaoUserInfoResponse;
import jungle.ovengers.model.response.Token;
import jungle.ovengers.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final String client_id = "0ec08fbf91f26056fcb7941c6f915a05";
    private final String redirect_uri = "http://localhost:8080/api/v1/members/kakao";
    private final String kakaoUri = "https://kauth.kakao.com";
    private final String kakaoApiUri = "https://kapi.kakao.com";
    public Token publishToken(String authCode) {
        KakaoTokenResponse kakaoTokenResponse = getKakaoTokenResponse(authCode);
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
        KakaoTokenResponse kakaoTokenResponse = webClient.post()
                                                         .uri(uriBuilder -> uriBuilder.path("/oauth/token")
                                                                                  .queryParam("grant_type", "authorization_code")
                                                                                  .queryParam("client_id", client_id)
                                                                                  .queryParam("code", authCode)
                                                                                  .queryParam("redirect_uri", redirect_uri)
                                                                                  .build())
                                                         .retrieve()
                                                         .bodyToMono(KakaoTokenResponse.class)
                                                         .block();
        return kakaoTokenResponse;
    }

}
