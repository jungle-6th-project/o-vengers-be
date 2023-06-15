package jungle.ovengers.service;

import jungle.ovengers.entity.MemberGroupEntity;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.MemberGroupRepository;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.repository.RoomRepository;
import jungle.ovengers.support.converter.RoomConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final MemberRoomRepository memberRoomRepository;
    public List<RoomResponse> getRooms(RoomBrowseRequest request) {
        List<Long> roomIds = roomRepository.findByGroupIdAndDeletedFalse(request.getGroupId())
                                           .stream()
                                           .map(RoomEntity::getId)
                                           .collect(Collectors.toList());

        for (Long roomId : roomIds) {
            List<MemberRoomEntity> memberRoomEntities = memberRoomRepository.findByRoomIdAndDeletedFalse(roomId);

        }


        return null;
    }
}
