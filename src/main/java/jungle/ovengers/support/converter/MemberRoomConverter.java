package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;

@UtilityClass
public class MemberRoomConverter {

    public static MemberRoomEntity to(Long memberId, RoomEntity roomEntity) {
        return MemberRoomEntity.builder()
                               .memberId(memberId)
                               .roomId(roomEntity.getId())
                               .durationTime(Duration.ZERO)
                               .startTime(roomEntity.getStartTime())
                               .endTime(roomEntity.getEndTime())
                               .build();
    }
}
