package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.response.RoomResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class RoomConverter {
    public static RoomEntity to(RoomAddRequest request, Long memberId) {
        return RoomEntity.builder()
                         .groupId(request.getGroupId())
                         .ownerId(memberId)
                         .startTime(request.getStartTime())
                         .endTime(request.getEndTime())
                         .deleted(false)
                         .build();
    }

    public static RoomResponse from(RoomEntity roomEntity, MemberRoomEntity memberRoomEntity, List<String> profiles) {
        return RoomResponse.builder()
                           .memberRoomId(memberRoomEntity.getId())
                           .roomId(roomEntity.getId())
                           .startTime(roomEntity.getStartTime())
                           .endTime(roomEntity.getEndTime())
                           .profiles(profiles)
                           .build();
    }
}
