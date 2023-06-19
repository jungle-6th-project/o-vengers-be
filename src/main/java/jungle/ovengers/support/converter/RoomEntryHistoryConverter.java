package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntryHistoryEntity;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class RoomEntryHistoryConverter {

    public static RoomEntryHistoryEntity to(MemberRoomEntity memberRoomEntity) {
        return RoomEntryHistoryEntity.builder()
                                     .memberRoomId(memberRoomEntity.getId())
                                     .enterTime(LocalDateTime.now())
                                     .build();
    }
}
