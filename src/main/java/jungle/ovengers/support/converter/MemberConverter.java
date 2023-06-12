package jungle.ovengers.support.converter;


import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.model.dto.MemberDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MemberConverter {
    public static MemberEntity to(MemberDto memberDto) {
        return MemberEntity.builder()
                           .name(memberDto.getName())
                           .profile(memberDto.getProfile())
                           .email(memberDto.getEmail())
                           .build();
    }

    public static MemberDto from(MemberEntity memberEntity) {
        return MemberDto.builder()
                        .name(memberEntity.getName())
                        .profile(memberEntity.getProfile())
                        .email(memberEntity.getEmail())
                        .build();
    }
}
