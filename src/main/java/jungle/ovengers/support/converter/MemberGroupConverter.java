package jungle.ovengers.support.converter;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberGroupEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class MemberGroupConverter {
    public MemberGroupEntity to(MemberEntity memberEntity, GroupEntity groupEntity) {
        return MemberGroupEntity.builder()
                                .groupId(groupEntity.getId())
                                .memberId(memberEntity.getId())
                                .deleted(false)
                                .build();
    }
}
