package jungle.ovengers.data;

import jungle.ovengers.entity.RoomEntity;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class FakeRoomInitializer {

    public static RoomEntity of(LocalDateTime time) {
        return RoomEntity.builder()
                         .id(1L)
                         .startTime(time)
                         .endTime(time.plusMinutes(25))
                         .ownerId(1L)
                         .groupId(1L)
                         .deleted(false)
                         .build();
    }

    public static RoomEntity of(Long roomId, Long groupId, Long ownerId, LocalDateTime time) {
        return RoomEntity.builder()
                         .id(roomId)
                         .startTime(time)
                         .endTime(time.plusMinutes(25))
                         .groupId(groupId)
                         .ownerId(ownerId)
                         .deleted(false)
                         .build();
    }
}
