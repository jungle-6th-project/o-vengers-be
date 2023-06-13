package jungle.ovengers.support.converter;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberGroupEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MemberGroupConverter {
    public MemberGroupEntity to(Long memberId, GroupEntity groupEntity) {
        return MemberGroupEntity.builder()
                                .groupId(groupEntity.getId())
                                .memberId(memberId)
                                .build();
    }
}
