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
public class RoomStompService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final MemberRoomRepository memberRoomRepository;

    public RoomResponse generateRoom(Long memberId, RoomAddRequest request) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                       .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));

        RoomEntity roomEntity = roomRepository.findByGroupIdAndStartTimeAndDeletedFalse(request.getGroupId(), request.getStartTime())
                                              .orElseGet(() -> roomRepository.save(RoomConverter.to(request, memberEntity)));

       memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomEntity.getId())
                                                                .orElseGet(() -> memberRoomRepository.save(MemberRoomConverter.to(memberId, roomEntity.getId())));

        return RoomConverter.from(roomEntity, List.of(memberEntity.getProfile()));
    }

    public RoomResponse joinRoom(Long memberId, RoomJoinRequest request) {


        MemberEntity memberEntity = memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                       .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                                              .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));
        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, request.getRoomId())
                                                                 .orElse(null);

        if (memberRoomEntity == null) {
            memberRoomRepository.save(MemberRoomConverter.to(memberId, request.getRoomId()));
            roomEntity.addProfile(memberEntity.getProfile());
            return RoomConverter.from(roomEntity, roomEntity.getProfiles());
        }

        memberRoomRepository.delete(memberRoomEntity);
        roomEntity.removeProfile(memberEntity.getProfile());
        if (!memberRoomRepository.existsByRoomIdAndDeletedFalse(request.getRoomId())) {
            roomEntity.delete();
            return new RoomResponse();
        }

        return RoomConverter.from(roomEntity, roomEntity.getProfiles());
    }
}

