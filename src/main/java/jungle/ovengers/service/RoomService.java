package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.entity.RoomEntryHistoryEntity;
import jungle.ovengers.exception.RoomNotFoundException;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.request.RoomHistoryRequest;
import jungle.ovengers.model.request.WholeRoomBrowseRequest;
import jungle.ovengers.model.response.RoomHistoryResponse;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.repository.RoomEntryHistoryRepository;
import jungle.ovengers.repository.RoomRepository;
import jungle.ovengers.support.converter.RoomConverter;
import jungle.ovengers.support.converter.RoomEntryHistoryConverter;
import jungle.ovengers.support.converter.RoomHistoryConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final RoomEntryHistoryRepository roomEntryHistoryRepository;
    private final AuditorHolder auditorHolder;
    private final String NOT_INVOLVED_ROOM = "사용자가 참여하고 있는 방이 아닙니다.";
    private final String INVALID_ENTER_TIME = "입장 가능한 시간이 아닙니다.";

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

    public RoomHistoryResponse generateRoomEntryHistory(RoomHistoryRequest request) {
        Long memberId = auditorHolder.get();
        Long roomId = request.getRoomId();
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(roomId)
                                              .orElseThrow(() -> new RoomNotFoundException(roomId));
        if (!roomEntity.isValidTime(LocalDateTime.now())) { // 입장 시간이 roomEntity에 설정된 endTime 보다 늦을 경우
            throw new IllegalArgumentException(INVALID_ENTER_TIME);
        }
        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)
                                                                .orElseThrow(() -> new IllegalArgumentException(NOT_INVOLVED_ROOM + "memberId :" + memberId + " roomId :" + roomId));

        return RoomHistoryConverter.from(roomEntryHistoryRepository.save(RoomEntryHistoryConverter.to(memberRoomEntity)));
    }

    public RoomHistoryResponse updateRoomExitHistory(RoomHistoryRequest request) {
        Long memberId = auditorHolder.get();
        Long roomId = request.getRoomId();
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(roomId)
                                              .orElseThrow(() -> new RoomNotFoundException(roomId));

        LocalDateTime exitTime = LocalDateTime.now();
        if (!roomEntity.isValidTime(LocalDateTime.now())) { // 나가는 시간이 roomEntity 에 설정된 endTime 보다 늦을 경우
            exitTime = roomEntity.getEndTime();
        }

        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)
                                                                .orElseThrow(() -> new IllegalArgumentException(NOT_INVOLVED_ROOM + "memberId :" + memberId + " roomId :" + roomId));

        RoomEntryHistoryEntity roomEntryHistoryEntity = roomEntryHistoryRepository.findByMemberRoomIdAndExitTimeIsNull(memberRoomEntity.getId())
                                                                                  .stream()
                                                                                  .max(Comparator.comparing(RoomEntryHistoryEntity::getEnterTime))
                                                                                  .orElseThrow(() -> new IllegalArgumentException(NOT_INVOLVED_ROOM + "memberId :" + memberId + " roomId :" + roomId));

        roomEntryHistoryEntity.updateExitTime(exitTime);
        memberRoomEntity.accumulateDuration(Duration.between(roomEntryHistoryEntity.getEnterTime(), roomEntryHistoryEntity.getExitTime()));
        return RoomHistoryConverter.from(roomEntryHistoryEntity);
    }
}
