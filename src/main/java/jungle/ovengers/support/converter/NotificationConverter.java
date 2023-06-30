package jungle.ovengers.support.converter;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.NotificationEntity;
import jungle.ovengers.entity.RoomEntity;
import lombok.experimental.UtilityClass;

import java.time.temporal.ChronoUnit;

@UtilityClass
public final class NotificationConverter {

    public NotificationEntity to(MemberEntity memberEntity, GroupEntity groupEntity, RoomEntity roomEntity) {
        return NotificationEntity.builder()
                                 .memberId(memberEntity.getId())
                                 .groupId(groupEntity.getId())
                                 .roomId(roomEntity.getId())
                                 .notificationTime(roomEntity.getStartTime().truncatedTo(ChronoUnit.MINUTES))
                                 .build();
    }
}
