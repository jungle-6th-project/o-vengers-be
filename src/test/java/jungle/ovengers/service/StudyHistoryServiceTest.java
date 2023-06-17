package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.model.request.StudyHistoryRequest;
import jungle.ovengers.model.response.StudyHistoryResponse;
import jungle.ovengers.repository.MemberRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StudyHistoryServiceTest {

    @Mock
    private MemberRoomRepository memberRoomRepository;

    @Mock
    private AuditorHolder auditorHolder;

    @InjectMocks
    private StudyHistoryService studyHistoryService;

    private Long memberId;
    private Long roomId;
    private LocalDateTime now;

    private MemberRoomEntity memberRoomEntity;

    @BeforeEach
    public void setup() {
        memberId = 1L;
        roomId = 1L;
        now = LocalDateTime.now();
        memberRoomEntity = MemberRoomEntity.builder()
                                           .roomId(roomId)
                                           .memberId(memberId)
                                           .deleted(false)
                                           .time(now)
                                           .durationTime(Duration.ofHours(3))
                                           .build();
    }

    @DisplayName("사용자가 당일 참여 했던 방에서의 누적 학습 시간이 잘 조회되는지 테스트")
    @Test
    public void testGetDailyDuration() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRoomRepository.findByMemberId(memberId)).thenReturn(List.of(memberRoomEntity));

        //when
        StudyHistoryResponse result = studyHistoryService.getDailyDuration(new StudyHistoryRequest(now.minusHours(1), now.plusHours(1)));

        //then
        assertThat(result.getDuration()).isEqualTo(Duration.ofHours(3));
    }
}