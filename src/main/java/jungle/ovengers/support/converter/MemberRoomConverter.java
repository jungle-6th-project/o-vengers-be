package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberRoomEntity;
import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class MemberRoomConverter {

    public static MemberRoomEntity to(Long memberId, Long roomId) {
        return MemberRoomEntity.builder()
                               .memberId(memberId)
                               .roomId(roomId)
                               .durationTime(Duration.ZERO)
                               .build();
    }
}
