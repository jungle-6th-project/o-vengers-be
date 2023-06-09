package jungle.ovengers.support.converter;


import jungle.ovengers.dto.MemberDto;
import jungle.ovengers.entity.MemberEntity;
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
                        .profile(memberEntity.getEmail())
                        .email(memberEntity.getEmail())
                        .build();
    }
}