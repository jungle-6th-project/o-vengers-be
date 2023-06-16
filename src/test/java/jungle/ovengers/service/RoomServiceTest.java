package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private MemberRoomRepository memberRoomRepository;

    @Mock
    private AuditorHolder auditorHolder;

    @InjectMocks
    private RoomService roomService;

    private Long roomId;
    private Long memberId;
    private Long groupId;
    private RoomEntity roomEntity;

    @BeforeEach
    public void setup() {
        roomId = 1L;
        memberId = 1L;
        groupId = 1L;
        roomEntity = RoomEntity.builder()
                               .id(roomId)
                               .startTime(LocalDateTime.now())
                               .endTime(LocalDateTime.now()
                                                     .plusHours(1))
                               .profiles(List.of("profile1", "profile2"))
                               .ownerId(memberId)
                               .deleted(false)
                               .groupId(groupId)
                               .build();
    }

    @DisplayName("속해있는 그룹의 전체 방 정보를 조회할때, 데이터가 잘 조회 되는지 테스트")
    @Test
    public void testBrowseGroupRooms() {
        //given
        when(roomRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(Collections.singletonList(roomEntity));
        //when
        List<RoomResponse> results = roomService.getRooms(new RoomBrowseRequest(groupId, LocalDateTime.now()
                                                                                                      .minusHours(3), LocalDateTime.now()
                                                                                                                                   .plusHours(3)));
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
                                               .startTime(LocalDateTime.now()
                                                                       .plusHours(2))
                                               .endTime(LocalDateTime.now()
                                                                     .plusHours(3))
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
        List<RoomResponse> results = roomService.getJoinedRooms(new RoomBrowseRequest(groupId, LocalDateTime.now()
                                                                                                            .minusHours(5), LocalDateTime.now()
                                                                                                                                         .plusHours(5)));
        //then
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)
                          .getRoomId()).isEqualTo(roomId);
    }
}
