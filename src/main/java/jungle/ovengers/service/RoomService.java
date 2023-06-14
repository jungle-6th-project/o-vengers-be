package jungle.ovengers.service;

import jungle.ovengers.config.security.filter.token.TokenResolver;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.exception.TokenInvalidException;
import jungle.ovengers.model.request.RoomAddRequest;
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

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final RoomRepository roomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final TokenResolver tokenResolver;

    public RoomResponse generateRoom(RoomAddRequest request) {
        Long memberId = tokenResolver.resolveToken(request.getAccessToken())
                                     .orElseThrow(() -> new TokenInvalidException("Invalid access token"));

        MemberEntity memberEntity = memberRepository.findById(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        groupRepository.findById(request.getGroupId())
                       .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));

        RoomEntity roomEntity = roomRepository.findByStartTimeAndDeletedFalse(request.getStartTime())
                                              .orElseGet(() -> roomRepository.save(RoomConverter.to(request, memberId)));

        MemberRoomEntity memberRoomEntity = memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomEntity.getId())
                                                                .orElseGet(() -> memberRoomRepository.save(MemberRoomConverter.to(memberId, roomEntity.getId())));

        return RoomConverter.from(roomEntity, memberRoomEntity, List.of(memberEntity.getProfile()));
    }
}

