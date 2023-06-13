package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberGroupEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class MemberGroupConverter {
    public MemberGroupEntity to(Long memberId, Long groupId) {
        return MemberGroupEntity.builder()
                                .groupId(groupId)
                                .memberId(memberId)
                                .createdAt(LocalDateTime.now())
                                .deleted(false)
                                .build();
    }
}
