package jungle.ovengers.support.converter;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.response.RoomResponse;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class RoomConverter {
    public static RoomEntity to(RoomAddRequest request, MemberEntity memberEntity) {
        List<String> profiles = new ArrayList<>();
        profiles.add(memberEntity.getProfile());
        return RoomEntity.builder()
                         .groupId(request.getGroupId())
                         .ownerId(memberEntity.getId())
                         .profiles(profiles)
                         .startTime(request.getStartTime())
                         .endTime(request.getEndTime())
                         .deleted(false)
                         .build();
    }

    public static RoomResponse from(RoomEntity roomEntity) {
        return RoomResponse.builder()
                           .roomId(roomEntity.getId())
                           .startTime(roomEntity.getStartTime())
                           .endTime(roomEntity.getEndTime())
                           .profiles(roomEntity.getProfiles())
                           .build();
    }
}
