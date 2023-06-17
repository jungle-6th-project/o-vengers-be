package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.request.WholeRoomBrowseRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.repository.RoomRepository;
import jungle.ovengers.support.converter.RoomConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final AuditorHolder auditorHolder;

    public List<RoomResponse> getRooms(RoomBrowseRequest request) {
        LocalDateTime from = request.getFrom();
        LocalDateTime to = request.getTo();

        return roomRepository.findByGroupIdAndDeletedFalse(request.getGroupId())
                             .stream()
                             .filter(room -> room.isAfter(from) && room.isBefore(to))
                             .map(RoomConverter::from)
                             .collect(Collectors.toList());
    }

    public List<RoomResponse> getJoinedRooms(RoomBrowseRequest request) {
        Long memberId = auditorHolder.get();
        List<Long> joinedRoomIds = getJoinedRoomIds(memberId, request.getGroupId());
        return getNonDeletedRoomsByIds(joinedRoomIds)
                .stream()
                .map(RoomConverter::from)
                .collect(Collectors.toList());
    }

    private List<Long> getJoinedRoomIds(Long memberId, Long groupId) {
        return roomRepository.findByGroupIdAndDeletedFalse(groupId)
                             .stream()
                             .map(RoomEntity::getId)
                             .filter(id -> isMemberInRoom(memberId, id))
                             .collect(Collectors.toList());
    }

    private boolean isMemberInRoom(Long memberId, Long roomId) {
        return memberRoomRepository.existsByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId);
    }

    private List<RoomEntity> getNonDeletedRoomsByIds(List<Long> roomIds) {
        return roomRepository.findAllByIdAndDeletedFalse(roomIds);
    }

    public List<RoomResponse> getRoomsByAllGroups(WholeRoomBrowseRequest request) {
        Long memberId = auditorHolder.get();
        List<Long> joinedRoomIds = memberRoomRepository.findByMemberIdAndDeletedFalse(memberId)
                                                       .stream()
                                                       .map(MemberRoomEntity::getRoomId)
                                                       .collect(Collectors.toList());

        List<RoomEntity> joinedRoomEntities = roomRepository.findAllByIdAndDeletedFalse(joinedRoomIds)
                                                            .stream()
                                                            .filter(roomEntity -> roomEntity.isAfter(request.getFrom()) && roomEntity.isBefore(request.getTo()))
                                                            .collect(Collectors.toList());

        return joinedRoomEntities.stream()
                                 .map(RoomConverter::from)
                                 .collect(Collectors.toList());
    }
}
