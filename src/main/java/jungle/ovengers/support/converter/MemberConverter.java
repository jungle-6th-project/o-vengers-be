package jungle.ovengers.support.converter;


import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.model.oauth.KakaoUserInfoResponse;
import jungle.ovengers.model.response.MemberResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MemberConverter {
    public static MemberEntity to(MemberResponse memberResponse) {
        return MemberEntity.builder()
                           .name(memberResponse.getName())
                           .profile(memberResponse.getProfile())
                           .email(memberResponse.getEmail())
                           .deleted(false)
                           .build();
    }

    public static MemberResponse from(MemberEntity memberEntity) {
        return MemberResponse.builder()
                        .name(memberEntity.getName())
                        .profile(memberEntity.getProfile())
                        .email(memberEntity.getEmail())
                        .build();
    }

    public static MemberEntity to(KakaoUserInfoResponse kakaoUserInfoResponse) {
        return MemberEntity.builder()
                           .certificationId(kakaoUserInfoResponse.getId())
                           .profile(kakaoUserInfoResponse.getKakaoAccount()
                                                         .getProfile()
                                                         .getProfileImageUrl())
                           .email(kakaoUserInfoResponse.getKakaoAccount()
                                                       .getEmail())
                           .name(kakaoUserInfoResponse.getKakaoAccount()
                                                      .getProfile()
                                                      .getNickname())
                           .deleted(false)
                           .build();
    }
}
