package jungle.ovengers.service;

import jungle.ovengers.data.*;
import jungle.ovengers.entity.*;
import jungle.ovengers.enums.MemberStatus;
import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.request.RoomJoinRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.*;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomStompServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private MemberRoomRepository memberRoomRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private RoomStompService roomService;
    private MemberEntity memberEntity;
    private GroupEntity groupEntity;
    private RoomEntity roomEntity;
    private MemberRoomEntity memberRoomEntity;

    private ClientEntity clientEntity;

    @BeforeEach
    public void setup() {
        LocalDateTime now = LocalDateTime.now();
        memberEntity = FakeMemberInitializer.of();
        groupEntity = FakeGroupInitializer.of();
        roomEntity = FakeRoomInitializer.of(now);
        memberRoomEntity = FakeMemberRoomInitializer.of(now);
        clientEntity = FakeClientInitializer.of();
    }

    @DisplayName("예약 방 생성 요청이 들어왔을때 잘 생성 되는지 테스트")
    @Test
    public void testGenerateRoom() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        LocalDateTime now = LocalDateTime.now();

        RoomAddRequest request = new RoomAddRequest(groupId, now, now.plusMinutes(25));
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));

        when(groupRepository.findByIdAndDeletedFalse(request.getGroupId())).thenReturn(Optional.of(groupEntity));

        when(roomRepository.findByGroupIdAndStartTimeAndDeletedFalse(groupId, request.getStartTime())).thenReturn(Optional.empty());
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(roomEntity);

        when(memberRoomRepository.save(any(MemberRoomEntity.class))).thenReturn(memberRoomEntity);
        when(clientRepository.findByMemberId(memberId)).thenReturn(Optional.of(clientEntity));

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

    @DisplayName("예약 방 생성 요청이 들어왔을때 예약시간이 현재 시간보다 이전일 경우 예외가 발생 되는지 테스트")
    @Test
    public void testGenerateRoomWithInvalidRequestTime() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        LocalDateTime now = LocalDateTime.now();
        RoomAddRequest request = new RoomAddRequest(groupId, now, now.plusMinutes(30));
        //when, then
        assertThatThrownBy(() -> roomService.generateRoom(memberId, request)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 방에 사용자가 이미 예약되어 있을 경우, 사용자 예약 정보가 잘 삭제 되는지 테스트")
    @Test
    public void testJoinRoom() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long roomId = roomEntity.getId();

        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(roomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(Optional.of(memberRoomEntity));
        when(memberRoomRepository.existsByRoomIdAndDeletedFalse(roomId)).thenReturn(true);
        when(memberRoomRepository.findByRoomIdAndDeletedFalse(roomEntity.getId())).thenReturn(Collections.singletonList(memberRoomEntity));
        when(memberRepository.findAllByMemberIdsAndStatus(Collections.singletonList(memberId), MemberStatus.REGULAR)).thenReturn(Collections.singletonList(memberEntity));
        //when
        RoomResponse result = roomService.joinRoom(memberId, new RoomJoinRequest(roomId, groupId));
        //then
        verify(memberRoomRepository, never()).save(any(MemberRoomEntity.class));
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getEndTime()).isEqualTo(roomEntity.getEndTime());
        assertThat(result.getStartTime()).isEqualTo(roomEntity.getStartTime());
        assertThat(result.getProfiles()
                         .size()).isEqualTo(1);
    }

    @DisplayName("예약 방에 사용자가 참가할 경우, 사용자 예약 정보가 잘 저장되는지 테스트")
    @Test
    public void testJoinRoomWhenAlreadyJoined() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long roomId = roomEntity.getId();

        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findByIdAndDeletedFalse(groupId)).thenReturn(Optional.of(groupEntity));
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(roomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(Optional.empty());
        when(memberRoomRepository.save(any(MemberRoomEntity.class))).thenReturn(memberRoomEntity);
        when(memberRoomRepository.findByRoomIdAndDeletedFalse(roomEntity.getId())).thenReturn(Collections.singletonList(memberRoomEntity));
        when(memberRepository.findAllByMemberIdsAndStatus(Collections.singletonList(memberId), MemberStatus.REGULAR)).thenReturn(Collections.singletonList(memberEntity));
        //when
        RoomResponse result = roomService.joinRoom(memberId, new RoomJoinRequest(roomId, groupId));
        //then
        verify(memberRoomRepository, never()).delete(any(MemberRoomEntity.class));
        assertThat(result.getProfiles()
                         .size()).isEqualTo(1);
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getEndTime()).isEqualTo(roomEntity.getEndTime());
        assertThat(result.getStartTime()).isEqualTo(roomEntity.getStartTime());
    }

    @DisplayName("예약한 방에 예약자가 한명도 없으면, 해당 방이 잘 삭제 되는지 테스트")
    @Test
    public void testDeleteRoomWhenNobodyInRoom() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long roomId = roomEntity.getId();

        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
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