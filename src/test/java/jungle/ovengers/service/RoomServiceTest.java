package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.entity.RankEntity;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.entity.RoomEntryHistoryEntity;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.request.RoomHistoryRequest;
import jungle.ovengers.model.response.RoomHistoryResponse;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.repository.RankRepository;
import jungle.ovengers.repository.RoomEntryHistoryRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private MemberRoomRepository memberRoomRepository;

    @Mock
    private RoomEntryHistoryRepository roomEntryHistoryRepository;

    @Mock
    private RankRepository rankRepository;

    @Mock
    private AuditorHolder auditorHolder;

    @InjectMocks
    private RoomService roomService;

    private LocalDateTime now;
    private Long roomId;
    private Long memberId;
    private Long groupId;
    private Long memberRoomId;
    private Long roomEntryHistoryId;
    private Long rankId;
    private RoomEntity roomEntity;

    private MemberRoomEntity memberRoomEntity;
    private RoomEntryHistoryEntity roomEntryHistoryEntity;
    private RankEntity rankEntity;

    @BeforeEach
    public void setup() {
        now = LocalDateTime.now();
        roomId = 1L;
        memberId = 1L;
        groupId = 1L;
        memberRoomId = 1L;
        roomEntryHistoryId = 1L;
        rankId = 1L;
        roomEntity = RoomEntity.builder()
                               .id(roomId)
                               .startTime(now)
                               .endTime(now.plusMinutes(25))
                               .profiles(List.of("profile1", "profile2"))
                               .ownerId(memberId)
                               .deleted(false)
                               .groupId(groupId)
                               .build();
        memberRoomEntity = MemberRoomEntity.builder()
                                           .startTime(roomEntity.getStartTime())
                                           .endTime(roomEntity.getEndTime())
                                           .memberId(memberId)
                                           .roomId(roomId)
                                           .durationTime(Duration.ZERO)
                                           .deleted(false)
                                           .id(memberRoomId)
                                           .build();
        roomEntryHistoryEntity = RoomEntryHistoryEntity.builder()
                                                       .memberRoomId(memberRoomId)
                                                       .enterTime(now.minusHours(1))
                                                       .exitTime(null)
                                                       .id(roomEntryHistoryId)
                                                       .build();

        rankEntity = RankEntity.builder()
                               .rankId(rankId)
                               .duration(Duration.ZERO)
                               .groupId(groupId)
                               .memberId(memberId)
                               .deleted(false)
                               .build();
    }

    @DisplayName("속해있는 그룹의 전체 방 정보를 조회할때, 데이터가 잘 조회 되는지 테스트")
    @Test
    public void testBrowseGroupRooms() {
        //given
        when(roomRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(Collections.singletonList(roomEntity));
        //when
        List<RoomResponse> results = roomService.getRooms(new RoomBrowseRequest(groupId, now.minusHours(3), now.plusHours(3)));
        //then
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)
                          .getRoomId()).isEqualTo(roomEntity.getId());
    }

    @DisplayName("속해있는 그룹내에서 사용자가 예약한 방만 조회가 잘 되는지 테스트")
    @Test
    public void testBrowseGroupRoomsWhichMemberMadeReservation() {
        //given
        Long otherRoomId = 2L;
        Long otherMember = 2L;
        RoomEntity otherRoomEntity = RoomEntity.builder()
                                               .id(otherRoomId)
                                               .startTime(now.plusHours(2))
                                               .endTime(now.plusHours(3))
                                               .profiles(List.of("profile3", "profile4"))
                                               .ownerId(otherMember)
                                               .deleted(false)
                                               .groupId(groupId)
                                               .build();

        when(auditorHolder.get()).thenReturn(memberId);
        when(roomRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(List.of(roomEntity, otherRoomEntity));
        when(memberRoomRepository.existsByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(true);
        when(memberRoomRepository.existsByMemberIdAndRoomIdAndDeletedFalse(memberId, otherRoomId)).thenReturn(false);
        when(roomRepository.findAllByIdAndDeletedFalse(List.of(roomId))).thenReturn(List.of(roomEntity));
        //when
        List<RoomResponse> results = roomService.getJoinedRooms(new RoomBrowseRequest(groupId, now.minusHours(5), now.plusHours(5)));
        //then
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)
                          .getRoomId()).isEqualTo(roomId);
    }

    @DisplayName("방 입장시 입장 시간 기록을 요청했을때, 입장 시간 기록이 잘 생성되는지 테스트")
    @Test
    public void testGenerateRoomEntryHistory() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(roomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(Optional.of(memberRoomEntity));
        when(roomEntryHistoryRepository.save(any(RoomEntryHistoryEntity.class))).thenReturn(roomEntryHistoryEntity);
        //when
        RoomHistoryResponse result = roomService.generateRoomEntryHistory(new RoomHistoryRequest(roomId));
        //then
        verify(roomEntryHistoryRepository, times(1)).save(any(RoomEntryHistoryEntity.class));
        assertThat(result.getEnterTime()).isEqualTo(roomEntryHistoryEntity.getEnterTime());
    }

    @DisplayName("방 입장 가능 시간이 지난 이후에 입장 시간 기록을 요청했을때, 예외가 발생되는지 테스트")
    @Test
    public void testGenerateRoomEntryHistoryWithInvalidEnterTime() {
        //given
        RoomEntity invalidRoomEntity = RoomEntity.builder()
                                                 .id(roomId)
                                                 .startTime(now.minusDays(1))
                                                 .endTime(now.minusDays(1)
                                                             .plusMinutes(25))
                                                 .profiles(List.of("profile1", "profile2"))
                                                 .ownerId(memberId)
                                                 .deleted(false)
                                                 .groupId(groupId)
                                                 .build();

        when(auditorHolder.get()).thenReturn(memberId);
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(invalidRoomEntity));
        //when
        assertThatThrownBy(() -> roomService.generateRoomEntryHistory(new RoomHistoryRequest(roomId))).isInstanceOf(IllegalArgumentException.class);
        //then
        verify(memberRoomRepository, never()).findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId);
    }

    @DisplayName("방 퇴장 시간전에 퇴장 시간 기록을 요청했을때, 퇴장 시간이 잘 저장되는지 테스트")
    @Test
    public void testUpdateRoomExitHistory() {
        //given
        LocalDateTime lateEnterTime = LocalDateTime.now();
        RoomEntryHistoryEntity recentlyRoomEntryHistoryEntity = RoomEntryHistoryEntity.builder()
                                                                                      .memberRoomId(memberRoomId)
                                                                                      .enterTime(lateEnterTime)
                                                                                      .exitTime(null)
                                                                                      .id(roomEntryHistoryId)
                                                                                      .build();
        when(auditorHolder.get()).thenReturn(memberId);
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(roomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId)).thenReturn(Optional.of(memberRoomEntity));
        when(roomEntryHistoryRepository.findByMemberRoomIdAndExitTimeIsNull(memberRoomId)).thenReturn(List.of(roomEntryHistoryEntity, recentlyRoomEntryHistoryEntity));
        //when
        RoomHistoryResponse result = roomService.updateRoomExitHistory(new RoomHistoryRequest(roomId));
        //then
        assertThat(result.getEnterTime()).isEqualTo(lateEnterTime);
    }

    @DisplayName("방 퇴장 시간이후에 퇴장 시간 기록을 요청했을때, 요청 시각이 아닌 기존 방에 예정되어있던 종료 시간으로 잘 기록되는지 테스트")
    @Test
    public void testUpdateRoomExitHistoryWhenAfterRoomExitTime() {
        //given
        RoomEntity testRoomEntity = RoomEntity.builder()
                                              .id(roomId)
                                              .startTime(now.minusHours(1))
                                              .endTime(now.minusHours(1)
                                                          .plusMinutes(25))
                                              .profiles(List.of("profile1", "profile2"))
                                              .ownerId(memberId)
                                              .deleted(false)
                                              .groupId(groupId)
                                              .build();
        when(auditorHolder.get()).thenReturn(memberId);
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(testRoomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId))
                .thenReturn(Optional.of(memberRoomEntity));
        when(roomEntryHistoryRepository.findByMemberRoomIdAndExitTimeIsNull(memberRoomId)).thenReturn(Collections.singletonList(roomEntryHistoryEntity));
        when(rankRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(Optional.of(rankEntity));
        //when
        RoomHistoryResponse result = roomService.updateRoomExitHistory(new RoomHistoryRequest(roomId));
        //then
        assertThat(result.getEnterTime()).isEqualTo(testRoomEntity.getStartTime());
        assertThat(result.getExitTime()).isEqualTo(testRoomEntity.getEndTime());
    }

    @DisplayName("사용자가 예약한 방들 중 현재 입장 가능한 가장 빠른 방이 잘 조회되는지 테스트")
    @Test
    public void testGetNearestRoom() {
        //given
        List<MemberRoomEntity> memberRoomEntities = new ArrayList<>();
        RoomEntity otherRoomEntity = RoomEntity.builder()
                                               .id(2L)
                                               .startTime(now)
                                               .endTime(now.plusMinutes(25))
                                               .profiles(List.of("profile1", "profile2"))
                                               .ownerId(memberId)
                                               .deleted(false)
                                               .groupId(groupId)
                                               .build();
        MemberRoomEntity otherMemberRoomEntity = MemberRoomEntity.builder()
                                                                 .startTime(otherRoomEntity.getStartTime()
                                                                                           .plusMinutes(25))
                                                                 .endTime(otherRoomEntity.getEndTime()
                                                                                         .plusMinutes(25))
                                                                 .memberId(memberId)
                                                                 .roomId(otherRoomEntity.getId())
                                                                 .durationTime(Duration.ZERO)
                                                                 .deleted(false)
                                                                 .id(2L)
                                                                 .build();
        memberRoomEntities.add(otherMemberRoomEntity);
        memberRoomEntities.add(memberRoomEntity);

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRoomRepository.findByMemberIdAndDeletedFalse(memberId)).thenReturn(memberRoomEntities);
        when(roomRepository.findByIdAndDeletedFalse(memberRoomEntity.getRoomId())).thenReturn(Optional.of(roomEntity));
        //when
        RoomResponse result = roomService.getNearestRoom();
        //then
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getEndTime()).isEqualTo(roomEntity.getEndTime());
    }
}
