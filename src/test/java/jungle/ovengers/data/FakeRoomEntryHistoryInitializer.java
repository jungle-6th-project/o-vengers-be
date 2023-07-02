package jungle.ovengers.data;

import jungle.ovengers.entity.RoomEntryHistoryEntity;

import java.time.LocalDateTime;

public final class FakeRoomEntryHistoryInitializer {

    public static RoomEntryHistoryEntity of(LocalDateTime time) {
        return RoomEntryHistoryEntity.builder()
                                     .memberRoomId(1L)
                                     .enterTime(time)
                                     .exitTime(time.plusMinutes(25))
                                     .id(1L)
                                     .build();
    }
}
