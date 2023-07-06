package jungle.ovengers.service;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.exception.RoomNotFoundException;
import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.request.RoomJoinRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.*;
import jungle.ovengers.support.converter.MemberRoomConverter;
import jungle.ovengers.support.converter.NotificationConverter;
import jungle.ovengers.support.converter.RoomConverter;
import jungle.ovengers.support.validator.RoomValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomStompService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final NotificationRepository notificationRepository;
    private final ClientRepository clientRepository;

    @CacheEvict(cacheNames = "groupRooms", key = "#request.groupId")
    public RoomResponse generateRoom(Long memberId, RoomAddRequest request) {
        RoomValidator.validateIfRoomTimeAfterNow(request);

        MemberEntity memberEntity = memberRepository.findByIdAndDeletedFalse(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));
        GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                                                 .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        RoomEntity roomEntity = roomRepository.findByGroupIdAndStartTimeAndDeletedFalse(request.getGroupId(), request.getStartTime())
                                              .orElse(null);
        if (roomEntity == null) {
            RoomEntity newRoomEntity = roomRepository.save(RoomConverter.to(request, memberEntity));
            this.saveAssociations(memberEntity, groupEntity, newRoomEntity);
            List<Long> memberIds = memberRoomRepository.findByRoomIdAndDeletedFalse(newRoomEntity.getId())
                                                       .stream()
                                                       .map(MemberRoomEntity::getMemberId)
                                                       .collect(Collectors.toList());
            return RoomConverter.from(newRoomEntity, memberIds);
        }
        List<Long> memberIds = memberRoomRepository.findByRoomIdAndDeletedFalse(roomEntity.getId())
                                                   .stream()
                                                   .map(MemberRoomEntity::getMemberId)
                                                   .collect(Collectors.toList());
        return RoomConverter.from(roomEntity, memberIds);
    }


    @CacheEvict(cacheNames = "groupRooms", key = "#request.groupId")
    public RoomResponse joinRoom(Long memberId, RoomJoinRequest request) {
        MemberEntity memberEntity = memberRepository.findByIdAndDeletedFalse(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));
        GroupEntity groupEntity = groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                                                 .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                                              .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));
        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, request.getRoomId())
                                                                .orElse(null);

        // 해당 방에 예약 정보가 없을 경우 -> 방에 사용자 정보 추가, 연관 데이터 추가
        if (memberRoomEntity == null) {
            roomEntity.addProfile(memberEntity.getProfile());
            this.saveAssociations(memberEntity, groupEntity, roomEntity);
            return RoomConverter.from(roomEntity);
        }

        // 해당 방에 예약 정보가 이미 있을 경우 -> 방에서 사용자 정보 삭제, 연관 데이터들 제거
        roomEntity.removeProfile(memberEntity.getProfile());
        this.deleteAssociations(memberEntity, roomEntity, memberRoomEntity);

        // 위 연관 사항으로 인해, 방에 사용자가 한 명도 없을 경우 -> 방을 삭제
        if (!memberRoomRepository.existsByRoomIdAndDeletedFalse(request.getRoomId())) {
            roomEntity.delete();
            return RoomConverter.from(roomEntity.getStartTime());
        }

        List<Long> memberIds = memberRoomRepository.findByRoomIdAndDeletedFalse(roomEntity.getId())
                                                   .stream()
                                                   .map(MemberRoomEntity::getMemberId)
                                                   .collect(Collectors.toList());

        return RoomConverter.from(roomEntity, memberIds);
    }

    private void deleteAssociations(MemberEntity memberEntity, RoomEntity roomEntity, MemberRoomEntity memberRoomEntity) {
        memberRoomEntity.delete();
        notificationRepository.deleteByMemberIdAndRoomId(memberEntity.getId(), roomEntity.getId());
    }

    private void saveAssociations(MemberEntity memberEntity, GroupEntity groupEntity, RoomEntity roomEntity) {
        memberRoomRepository.save(MemberRoomConverter.to(memberEntity.getId(), roomEntity));
        clientRepository.findByMemberId(memberEntity.getId())
                        .ifPresent(clientEntity -> notificationRepository.save(NotificationConverter.to(memberEntity, groupEntity, roomEntity)));
    }
}

