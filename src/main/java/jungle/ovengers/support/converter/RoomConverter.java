package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.response.RoomResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class RoomConverter {
    public static RoomEntity to(RoomAddRequest request, MemberEntity memberEntity) {
        List<String> profiles = new ArrayList<>();
        profiles.add(memberEntity.getProfile());
        return RoomEntity.builder()
                         .groupId(request.getGroupId())
                         .ownerId(memberEntity.getId())
                         .startTime(request.getStartTime())
                         .endTime(request.getEndTime())
                         .deleted(false)
                         .build();
    }
    public static RoomResponse from(RoomEntity roomEntity) {
        if (roomEntity == null) {
            return null;
        }

        return RoomResponse.builder()
                           .roomId(roomEntity.getId())
                           .startTime(roomEntity.getStartTime())
                           .endTime(roomEntity.getEndTime())
                           .groupId(roomEntity.getGroupId())
                           .build();
    }

    public static RoomResponse from(RoomEntity roomEntity, List<Long> memberIds) {
        if (roomEntity == null) {
            return null;
        }

        return RoomResponse.builder()
                           .roomId(roomEntity.getId())
                           .startTime(roomEntity.getStartTime())
                           .endTime(roomEntity.getEndTime())
                           .groupId(roomEntity.getGroupId())
                           .memberIds(memberIds)
                           .build();
    }

    public static RoomResponse from(RoomEntity roomEntity, List<Long> memberIds, List<String> profiles) {
        if (roomEntity == null) {
            return null;
        }

        return RoomResponse.builder()
                           .roomId(roomEntity.getId())
                           .startTime(roomEntity.getStartTime())
                           .endTime(roomEntity.getEndTime())
                           .groupId(roomEntity.getGroupId())
                           .profiles(profiles)
                           .memberIds(memberIds)
                           .build();
    }

    public static RoomResponse from(LocalDateTime startTime) {
        return RoomResponse.builder()
                           .startTime(startTime)
                           .build();
    }
}
