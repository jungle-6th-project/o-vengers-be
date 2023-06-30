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
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            roomEntity = roomRepository.save(RoomConverter.to(request, memberEntity));
            memberRoomRepository.save(MemberRoomConverter.to(memberId, roomEntity));
            notificationRepository.save(NotificationConverter.to(memberEntity, groupEntity, roomEntity));
        }

        return RoomConverter.from(roomEntity);
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

        if (memberRoomEntity == null) {
            roomEntity.addProfile(memberEntity.getProfile());
            memberRoomRepository.save(MemberRoomConverter.to(memberId, roomEntity));
            notificationRepository.save(NotificationConverter.to(memberEntity, groupEntity, roomEntity));
            return RoomConverter.from(roomEntity);
        }

        notificationRepository.deleteByMemberIdAndRoomId(memberEntity.getId(), roomEntity.getId());
        memberRoomEntity.delete();
        roomEntity.removeProfile(memberEntity.getProfile());
        if (!memberRoomRepository.existsByRoomIdAndDeletedFalse(request.getRoomId())) {
            roomEntity.delete();
            return RoomConverter.from(roomEntity.getStartTime());
        }
        return RoomConverter.from(roomEntity);
    }
}

