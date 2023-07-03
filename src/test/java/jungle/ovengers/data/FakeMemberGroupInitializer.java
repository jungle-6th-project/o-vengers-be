package jungle.ovengers.data;

import jungle.ovengers.entity.MemberGroupEntity;

public final class FakeMemberGroupInitializer {
    public static MemberGroupEntity of() {
        return MemberGroupEntity.builder()
                                .id(1L)
                                .memberId(1L)
                                .groupId(1L)
                                .deleted(false)
                                .color("color")
                                .build();
    }
}
