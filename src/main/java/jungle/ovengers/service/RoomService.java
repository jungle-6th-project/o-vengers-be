package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.entity.RoomEntryHistoryEntity;
import jungle.ovengers.enums.MemberStatus;
import jungle.ovengers.exception.RoomNotFoundException;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.request.RoomHistoryRequest;
import jungle.ovengers.model.request.WholeRoomBrowseRequest;
import jungle.ovengers.model.response.RoomHistoryResponse;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.*;
import jungle.ovengers.support.converter.RoomConverter;
import jungle.ovengers.support.converter.RoomEntryHistoryConverter;
import jungle.ovengers.support.converter.RoomHistoryConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final RoomEntryHistoryRepository roomEntryHistoryRepository;
    private final RankRepository rankRepository;
    private final MemberRepository memberRepository;
    private final AuditorHolder auditorHolder;
    private final String NOT_INVOLVED_ROOM = "사용자가 참여하고 있는 방이 아닙니다.";
    private final String INVALID_ENTER_TIME = "입장 가능한 시간이 아닙니다.";
    private final int PPOMODORO_TIME = 25;

    @Cacheable(cacheNames = "groupRooms", key = "#request.groupId")
    public List<RoomResponse> getRooms(RoomBrowseRequest request) {
        LocalDateTime from = request.getFrom();
        LocalDateTime to = request.getTo();


        List<RoomEntity> roomEntities = roomRepository.findByGroupIdAndDeletedFalse(request.getGroupId())
                                                      .stream()
                                                      .filter(room -> room.isAfter(from) && room.isBefore(to))
                                                      .collect(Collectors.toList());

        Map<RoomEntity, List<Long>> roomMembers = new HashMap<>();
        for (RoomEntity roomEntity : roomEntities) {
            List<Long> memberIds = memberRoomRepository.findByRoomIdAndDeletedFalse(roomEntity.getId())
                                                     .stream()
                                                     .map(MemberRoomEntity::getMemberId)
                                                     .collect(Collectors.toList());
            roomMembers.put(roomEntity, memberIds);
        }

        List<RoomResponse> responses = new ArrayList<>();
        for (RoomEntity roomEntity : roomMembers.keySet()) {
            List<Long> memberIds = roomMembers.get(roomEntity);
            List<String> profiles = memberRepository.findAllByMemberIdsAndStatus(memberIds, MemberStatus.REGULAR)
                                                   .stream()
                                                   .map(MemberEntity::getProfile)
                                                   .collect(Collectors.toList());
            responses.add(RoomConverter.from(roomEntity, memberIds, profiles));
        }

        return responses;
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

        List<RoomResponse> responses = new ArrayList<>();
        for (RoomEntity joinedRoomEntity : joinedRoomEntities) {
            List<Long> memberIds = memberRoomRepository.findByRoomIdAndDeletedFalse(joinedRoomEntity.getId())
                                                     .stream()
                                                     .map(MemberRoomEntity::getMemberId)
                                                     .collect(Collectors.toList());
            List<String> profiles = memberRepository.findAllByMemberIdsAndStatus(memberIds, MemberStatus.REGULAR)
                                                   .stream()
                                                   .map(MemberEntity::getProfile)
                                                   .collect(Collectors.toList());
            responses.add(RoomConverter.from(joinedRoomEntity, memberIds, profiles));
        }

        return responses;
    }

    public RoomHistoryResponse generateRoomEntryHistory(RoomHistoryRequest request) {
        Long memberId = auditorHolder.get();
        Long roomId = request.getRoomId();
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(roomId)
                                              .orElseThrow(() -> new RoomNotFoundException(roomId));
        LocalDateTime enterTime = LocalDateTime.now();
        if (roomEntity.isBeforeStartTime(enterTime)) { // 입장 시간이 roomEntity에 설정된 startTime 보다 빠를 경우
            enterTime = roomEntity.getStartTime();
        }

        if (roomEntity.isAfterEndTime(enterTime)) { // 입장 시간이 roomEntity에 설정된 endTime 보다 늦을 경우
            throw new IllegalArgumentException(INVALID_ENTER_TIME);
        }
        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)
                                                                .orElseThrow(() -> new IllegalArgumentException(NOT_INVOLVED_ROOM + "memberId :" + memberId + " roomId :" + roomId));

        return RoomHistoryConverter.from(roomEntryHistoryRepository.save(RoomEntryHistoryConverter.to(memberRoomEntity, enterTime)));
    }

    @CacheEvict(cacheNames = "dailyDuration", key = "{#root.target.makeRedisKey()}")
    public RoomHistoryResponse updateRoomExitHistory(RoomHistoryRequest request) {
        log.info("Cache Evict Study History");
        Long memberId = auditorHolder.get();
        Long roomId = request.getRoomId();
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(roomId)
                                              .orElseThrow(() -> new RoomNotFoundException(roomId));

        LocalDateTime exitTime = LocalDateTime.now();

        if (roomEntity.isBeforeStartTime(exitTime)) { // 나가는 시간이 roomEntity에 설정된 startTime 보다 빠를 경우
            return null;
        }

        if (roomEntity.isAfterEndTime(LocalDateTime.now())) { // 나가는 시간이 roomEntity에 설정된 endTime 보다 늦을 경우
            exitTime = roomEntity.getEndTime();
        }

        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)
                                                                .orElseThrow(() -> new IllegalArgumentException(NOT_INVOLVED_ROOM + "memberId :" + memberId + " roomId :" + roomId));

        RoomEntryHistoryEntity roomEntryHistoryEntity = roomEntryHistoryRepository.findByMemberRoomIdAndExitTimeIsNull(memberRoomEntity.getId())
                                                                                  .stream()
                                                                                  .max(Comparator.comparing(RoomEntryHistoryEntity::getEnterTime))
                                                                                  .orElseThrow(() -> new IllegalArgumentException(NOT_INVOLVED_ROOM + "memberId :" + memberId + " roomId :" + roomId));

        roomEntryHistoryEntity.updateExitTime(exitTime);

        Duration calculatedDuration = Duration.between(roomEntryHistoryEntity.getEnterTime(), roomEntryHistoryEntity.getExitTime());
        memberRoomEntity.plusDuration(calculatedDuration);
        rankRepository.findByGroupIdAndMemberIdAndDeletedFalse(roomEntity.getGroupId(), memberId)
                      .ifPresent(rank -> rank.plusDuration(calculatedDuration));

        return RoomHistoryConverter.from(roomEntryHistoryEntity);
    }

    /**
     * 방의 종료 시간이 현재 시간 보다 늦은 방 예약들 중, 현재 시간과 가장 가까운 방을 조회함.
     */
    public RoomResponse getNearestRoom() {
        Long memberId = auditorHolder.get();
        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndDeletedFalse(memberId)
                                                                .stream()
                                                                .filter(memberRoom -> LocalDateTime.now()
                                                                                                   .isBefore(memberRoom.getStartTime()
                                                                                                                       .plusMinutes(PPOMODORO_TIME)))
                                                                .min(Comparator.comparing(memberRoom -> memberRoom.getStartTime()
                                                                                                                  .plusMinutes(PPOMODORO_TIME)))
                                                                .orElse(null);

        if (memberRoomEntity == null) {
            return null;
        }

        return RoomConverter.from(roomRepository.findByIdAndDeletedFalse(memberRoomEntity.getRoomId())
                                                .orElse(null));
    }

    public void deleteExpiredRoom() {
        roomRepository.findByDeletedFalseAndEndTimeBefore(LocalDateTime.now())
                      .forEach(RoomEntity::delete);
    }
}
