package jungle.ovengers.service;

import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.response.RoomResponse;
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
        List<RoomResponse> results = roomService.getRooms(new RoomBrowseRequest(groupId));
        //then
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)
                          .getRoomId()).isEqualTo(roomEntity.getId());
    }
}
