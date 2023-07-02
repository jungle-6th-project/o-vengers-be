package jungle.ovengers.data;

import jungle.ovengers.entity.MemberRoomEntity;

import java.time.Duration;
import java.time.LocalDateTime;

public final class FakeMemberRoomInitializer {

    public static MemberRoomEntity of(LocalDateTime time) {
        return MemberRoomEntity.builder()
                               .id(1L)
                               .memberId(1L)
                               .roomId(1L)
                               .durationTime(Duration.ZERO)
                               .startTime(time)
                               .endTime(time.plusMinutes(25))
                               .deleted(false)
                               .build();
    }

    public static MemberRoomEntity of(Long memberRoomId, Long roomId, Long memberId, LocalDateTime time) {
        return MemberRoomEntity.builder()
                               .id(memberRoomId)
                               .memberId(memberId)
                               .roomId(roomId)
                               .durationTime(Duration.ZERO)
                               .startTime(time)
                               .endTime(time.plusMinutes(25))
                               .deleted(false)
                               .build();
    }
}
