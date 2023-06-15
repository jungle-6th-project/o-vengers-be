package jungle.ovengers.service;

import jungle.ovengers.config.security.filter.token.TokenResolver;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.request.RoomJoinRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private MemberRoomRepository memberRoomRepository;

    @Mock
    private TokenResolver tokenResolver;

    @InjectMocks
    private RoomStompService roomService;

    private Long memberId;
    private Long groupId;
    private Long roomId;
    private Long memberRoomId;
    private MemberEntity memberEntity;
    private GroupEntity groupEntity;
    private RoomEntity roomEntity;
    private MemberRoomEntity memberRoomEntity;

    @BeforeEach
    public void setup() {
        memberId = 1L;
        groupId = 1L;
        roomId = 1L;
        memberRoomId = 1L;
        memberEntity = MemberEntity.builder()
                                   .id(memberId)
                                   .email("email")
                                   .profile("profile")
                                   .name("name")
                                   .deleted(false)
                                   .build();
        groupEntity = GroupEntity.builder()
                                 .id(groupId)
                                 .ownerId(memberId)
                                 .path("path")
                                 .groupName("groupName")
                                 .isSecret(false)
                                 .createdAt(LocalDateTime.now())
                                 .deleted(false)
                                 .build();
        roomEntity = RoomEntity.builder()
                               .id(roomId)
                               .startTime(LocalDateTime.now())
                               .endTime(LocalDateTime.now()
                                                     .plusHours(1))
                               .ownerId(memberId)
                               .groupId(groupId)
                               .profiles(new ArrayList<>())
                               .deleted(false)
                               .build();
        memberRoomEntity = MemberRoomEntity.builder()
                                           .id(memberRoomId)
                                           .durationTime(Duration.ZERO)
                                           .memberId(memberId)
                                           .deleted(false)
                                           .build();
    }

    @DisplayName("예약 방 생성 요청이 들어왔을때 잘 생성 되는지 테스트")
    @Test
    public void testGenerateRoom() {
        //given
        Long memberId = 1L;
        RoomAddRequest request = new RoomAddRequest(groupId, LocalDateTime.now(), LocalDateTime.now()
                                                                                               .plusHours(1));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));

        when(groupRepository.findByIdAndDeletedFalse(request.getGroupId())).thenReturn(Optional.of(groupEntity));

        when(roomRepository.findByGroupIdAndStartTimeAndDeletedFalse(groupId, request.getStartTime())).thenReturn(Optional.empty());
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(roomEntity);

        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomEntity.getId())).thenReturn(Optional.empty());
        when(memberRoomRepository.save(any(MemberRoomEntity.class))).thenReturn(memberRoomEntity);

        //when
        RoomResponse result = roomService.generateRoom(memberId, request);

        //then
        assertNotNull(result);
        assertThat(result.getStartTime()
                         .getHour()).isEqualTo(request.getStartTime()
                                                      .getHour());
        assertThat(result.getStartTime()
                         .getMinute()).isEqualTo(request.getStartTime()
                                                        .getMinute());
        assertThat(result.getRoomId()).isEqualTo(roomEntity.getId());
    }

    @DisplayName("예약 방에 사용자가 이미 예약되어 있을 경우, 사용자 예약 정보가 잘 삭제 되는지 테스트")
    @Test
    public void testJoinRoom() {
        //given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(roomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(Optional.of(memberRoomEntity));
        when(memberRoomRepository.existsByRoomIdAndDeletedFalse(roomId)).thenReturn(true);
        //when
        RoomResponse result = roomService.joinRoom(memberId, new RoomJoinRequest(roomId, groupId));
        //then
        verify(memberRoomRepository, never()).save(any(MemberRoomEntity.class));
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getEndTime()).isEqualTo(roomEntity.getEndTime());
        assertThat(result.getStartTime()).isEqualTo(roomEntity.getStartTime());
        assertThat(result.getProfiles().size()).isEqualTo(0);
    }

    @DisplayName("예약 방에 사용자가 참가할 경우, 사용자 예약 정보가 잘 저장되는지 테스트")
    @Test
    public void testJoinRoomWhenAlreadyJoined() {
        //given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(roomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(Optional.empty());
        when(memberRoomRepository.save(any(MemberRoomEntity.class))).thenReturn(memberRoomEntity);
        //when
        RoomResponse result = roomService.joinRoom(memberId, new RoomJoinRequest(roomId, groupId));
        //then
        verify(memberRoomRepository, never()).delete(any(MemberRoomEntity.class));
        assertThat(result.getProfiles().size()).isEqualTo(1);
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getEndTime()).isEqualTo(roomEntity.getEndTime());
        assertThat(result.getStartTime()).isEqualTo(roomEntity.getStartTime());
    }

    @DisplayName("예약한 방에 예약자가 한명도 없으면, 해당 방이 잘 삭제 되는지 테스트")
    @Test
    public void testDeleteRoomWhenNobodyInRoom() {
        //given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(roomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(Optional.of(memberRoomEntity));
        when(memberRoomRepository.existsByRoomIdAndDeletedFalse(roomId)).thenReturn(false);
        //when
        RoomResponse result = roomService.joinRoom(memberId, new RoomJoinRequest(roomId, groupId));
        //then
        assertThat(roomEntity.isDeleted()).isTrue();
        verify(memberRoomRepository, never()).findByRoomIdAndDeletedFalse(roomId);
        verify(memberRepository, never()).findAllById(Collections.singletonList(memberId));
    }
}