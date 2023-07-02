package jungle.ovengers.data;

import jungle.ovengers.entity.MemberEntity;

public final class FakeMemberInitializer {

    public static MemberEntity of() {
        return MemberEntity.builder()
                           .id(1L)
                           .email("email")
                           .profile("profile1")
                           .name("name")
                           .deleted(false)
                           .build();
    }

    public static MemberEntity of(Long memberId) {
        return MemberEntity.builder()
                           .id(memberId)
                           .email("email")
                           .profile("profile1")
                           .name("name")
                           .deleted(false)
                           .build();
    }
}
