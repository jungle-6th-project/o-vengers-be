package jungle.ovengers.service;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.exception.RoomNotFoundException;
import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.request.RoomJoinRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.repository.RoomRepository;
import jungle.ovengers.support.converter.MemberRoomConverter;
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

    @CacheEvict(cacheNames = "groupRooms", key = "#request.groupId")
    public RoomResponse generateRoom(Long memberId, RoomAddRequest request) {
        RoomValidator.validateIfRoomTimeAfterNow(request);

        MemberEntity memberEntity = memberRepository.findByIdAndDeletedFalse(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                       .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));

        RoomEntity roomEntity = roomRepository.findByGroupIdAndStartTimeAndDeletedFalse(request.getGroupId(), request.getStartTime())
                                              .orElse(null);
        if (roomEntity == null) {
            roomEntity = roomRepository.save(RoomConverter.to(request, memberEntity));
            memberRoomRepository.save(MemberRoomConverter.to(memberId, roomEntity));
        }

        return RoomConverter.from(roomEntity);
    }

    @CacheEvict(cacheNames = "groupRooms", key = "#request.groupId")
    public RoomResponse joinRoom(Long memberId, RoomJoinRequest request) {
        MemberEntity memberEntity = memberRepository.findByIdAndDeletedFalse(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                       .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                                              .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));
        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, request.getRoomId())
                                                                .orElse(null);

        if (memberRoomEntity == null) {
            memberRoomRepository.save(MemberRoomConverter.to(memberId, roomEntity));
            roomEntity.addProfile(memberEntity.getProfile());
            return RoomConverter.from(roomEntity);
        }

        memberRoomEntity.delete();
        roomEntity.removeProfile(memberEntity.getProfile());
        if (!memberRoomRepository.existsByRoomIdAndDeletedFalse(request.getRoomId())) {
            roomEntity.delete();
            return RoomConverter.from(roomEntity.getStartTime());
        }
        return RoomConverter.from(roomEntity);
    }
}

