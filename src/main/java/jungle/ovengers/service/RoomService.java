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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

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
                                              .orElseGet(() -> roomRepository.save(RoomConverter.to(request, memberId)));

        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomEntity.getId())
                                                                .orElseGet(() -> memberRoomRepository.save(MemberRoomConverter.to(memberId, roomEntity.getId())));

        return RoomConverter.from(roomEntity, memberRoomEntity, List.of(memberEntity.getProfile()));
    }

    public RoomResponse joinRoom(Long memberId, RoomJoinRequest request) {
        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        groupRepository.findByIdAndDeletedFalse(request.getGroupId())
                       .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        RoomEntity roomEntity = roomRepository.findByIdAndDeletedFalse(request.getRoomId())
                                              .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));
        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, request.getRoomId())
                                                                 .orElse(null);

        if (memberRoomEntity == null) {
            memberRoomEntity = memberRoomRepository.save(MemberRoomConverter.to(memberId, request.getRoomId()));

            return getRoomResponse(request, roomEntity, memberRoomEntity);
        }

        memberRoomRepository.delete(memberRoomEntity);

        return getRoomResponse(request, roomEntity, memberRoomEntity);
    }

    private RoomResponse getRoomResponse(RoomJoinRequest request, RoomEntity roomEntity, MemberRoomEntity memberRoomEntity) {
        List<Long> memberIds = memberRoomRepository.findByRoomIdAndDeletedFalse(request.getRoomId())
                                                   .stream()
                                                   .map(MemberRoomEntity::getMemberId)
                                                   .collect(Collectors.toList());
        List<String> profiles = memberRepository.findAllById(memberIds)
                                                .stream()
                                                .map(MemberEntity::getProfile)
                                                .collect(Collectors.toList());

        return RoomConverter.from(roomEntity, memberRoomEntity, profiles);
    }
}

