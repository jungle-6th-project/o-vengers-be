package jungle.ovengers.support.converter;


import jungle.ovengers.model.response.MemberResponse;
import jungle.ovengers.entity.MemberEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MemberConverter {
    public static MemberEntity to(MemberResponse memberResponse) {
        return MemberEntity.builder()
                           .name(memberResponse.getName())
                           .profile(memberResponse.getProfile())
                           .email(memberResponse.getEmail())
                           .build();
    }

    public static MemberResponse from(MemberEntity memberEntity) {
        return MemberResponse.builder()
                             .name(memberEntity.getName())
                             .profile(memberEntity.getEmail())
                             .email(memberEntity.getEmail())
                             .build();
    }
}
