package jungle.ovengers.support.converter;


import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.model.dto.MemberDto;
import jungle.ovengers.model.oauth.KakaoUserInfoResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MemberConverter {
    public static MemberEntity to(MemberDto memberDto) {
        return MemberEntity.builder()
                           .name(memberDto.getName())
                           .profile(memberDto.getProfile())
                           .email(memberDto.getEmail())
                           .deleted(false)
                           .build();
    }

    public static MemberDto from(MemberEntity memberEntity) {
        return MemberDto.builder()
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
