package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.data.*;
import jungle.ovengers.entity.*;
import jungle.ovengers.enums.MemberStatus;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.request.RoomHistoryRequest;
import jungle.ovengers.model.response.RoomHistoryResponse;
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
import java.time.LocalDate;
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
    private MemberRepository memberRepository;
    @Mock
    private AuditorHolder auditorHolder;
    @InjectMocks
    private RoomService roomService;

    private LocalDateTime now;
    private Long memberId;
    private Long groupId;
    private RoomEntity roomEntity;
    private MemberRoomEntity memberRoomEntity;
    private RoomEntryHistoryEntity roomEntryHistoryEntity;
    private RankEntity rankEntity;
    private MemberEntity memberEntity;

    @BeforeEach
    public void setup() {
        now = LocalDateTime.now();
        memberId = 1L;
        groupId = 1L;
        roomEntity = FakeRoomInitializer.of(now);
        memberRoomEntity = FakeMemberRoomInitializer.of(now);
        roomEntryHistoryEntity = FakeRoomEntryHistoryInitializer.of(now);
        rankEntity = FakeRankInitializer.of();
        memberEntity = FakeMemberInitializer.of();
    }

    @DisplayName("속해있는 그룹의 전체 방 정보를 조회할때, 데이터가 잘 조회 되는지 테스트")
    @Test
    public void testBrowseGroupRooms() {
        //given
        when(roomRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(Collections.singletonList(roomEntity));
        when(memberRepository.findAllByMemberIdsAndStatus(anyList(), any())).thenReturn(Collections.singletonList(memberEntity));
        //when
        List<RoomResponse> results = roomService.getRooms(new RoomBrowseRequest(groupId, now.minusHours(3), now.plusHours(3)));
        //then
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)
                          .getRoomId()).isEqualTo(roomEntity.getId());
    }

    @DisplayName("방 입장시 입장 시간 기록을 요청했을때, 입장 시간 기록이 잘 생성되는지 테스트")
    @Test
    public void testGenerateRoomEntryHistory() {
        //given
        Long roomId = roomEntity.getId();

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
        Long roomId = roomEntity.getId();
        LocalDateTime invalidTime = now.minusDays(1);
        RoomEntity invalidRoomEntity = FakeRoomInitializer.of(invalidTime);

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
        Long roomId = roomEntity.getId();
        Long memberRoomId = memberRoomEntity.getId();

        LocalDateTime lateEnterTime = LocalDateTime.now();
        RoomEntryHistoryEntity recentlyRoomEntryHistoryEntity = FakeRoomEntryHistoryInitializer.of(lateEnterTime)
                                                                                               .toBuilder()
                                                                                               .exitTime(null)
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
        Long roomId = roomEntity.getId();
        Long memberRoomId = memberRoomEntity.getId();

        RoomEntity testRoomEntity = FakeRoomInitializer.of(now.minusHours(1));
        RoomEntryHistoryEntity testRoomEntryHistoryEntity = FakeRoomEntryHistoryInitializer.of(now.minusHours(1));

        when(auditorHolder.get()).thenReturn(memberId);
        when(roomRepository.findByIdAndDeletedFalse(roomId)).thenReturn(Optional.of(testRoomEntity));
        when(memberRoomRepository.findByMemberIdAndRoomIdAndDeletedFalse(memberId, roomId))
                .thenReturn(Optional.of(memberRoomEntity));
        when(roomEntryHistoryRepository.findByMemberRoomIdAndExitTimeIsNull(memberRoomId)).thenReturn(Collections.singletonList(testRoomEntryHistoryEntity));
        when(rankRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(Optional.of(rankEntity));
        //when
        RoomHistoryResponse result = roomService.updateRoomExitHistory(new RoomHistoryRequest(roomId));
        //then
        assertThat(result.getExitTime()).isEqualTo(testRoomEntity.getEndTime());
    }

    @DisplayName("사용자가 예약한 방들 중 현재 입장 가능한 가장 빠른 방이 잘 조회되는지 테스트")
    @Test
    public void testGetNearestRoom() {
        //given
        Long roomId = roomEntity.getId();
        RoomEntity otherRoomEntity = FakeRoomInitializer.of(roomId + 1L, groupId, memberId, LocalDateTime.now()
                                                                                                         .plusDays(1));
        MemberRoomEntity otherMemberRoomEntity = FakeMemberRoomInitializer.of(memberRoomEntity.getId() + 1L, otherRoomEntity.getId(), memberId, otherRoomEntity.getStartTime());

        List<MemberRoomEntity> memberRoomEntities = new ArrayList<>();
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
