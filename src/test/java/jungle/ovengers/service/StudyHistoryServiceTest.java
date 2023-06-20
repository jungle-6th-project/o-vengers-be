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
    private Long otherRoomId;
    private LocalDateTime now;

    private MemberRoomEntity memberRoomEntity;
    private MemberRoomEntity memberRoomEntity2;

    @BeforeEach
    public void setup() {
        memberId = 1L;
        roomId = 1L;
        otherRoomId = 2L;
        now = LocalDateTime.now();
        memberRoomEntity = MemberRoomEntity.builder()
                                           .roomId(roomId)
                                           .memberId(memberId)
                                           .deleted(false)
                                           .startTime(now)
                                           .endTime(now.plusMinutes(25))
                                           .durationTime(Duration.ofHours(3))
                                           .build();
        memberRoomEntity2 = MemberRoomEntity.builder()
                                            .roomId(otherRoomId)
                                            .memberId(memberId)
                                            .deleted(false)
                                            .startTime(now.plusDays(1))
                                            .endTime(now.plusDays(1)
                                                        .plusMinutes(25))
                                            .durationTime(Duration.ofHours(1))
                                            .build();
    }

    @DisplayName("사용자가 당일 참여 했던 방에서의 누적 학습 시간이 잘 조회되는지 테스트")
    @Test
    public void testGetDailyDuration() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRoomRepository.findByMemberId(memberId)).thenReturn(List.of(memberRoomEntity));

        //when
        List<StudyHistoryResponse> result = studyHistoryService.getDailyDuration(new StudyHistoryRequest(now.minusHours(1), now.plusHours(1)));

        //then
        assertThat(result.get(0).getDuration()).isEqualTo(Duration.ofHours(3));
    }

    @DisplayName("사용자가 여러 기간 동안 참여 했던 방에서의 누적 학습 시간이 잘 조회 되는지 테스트")
    @Test
    public void testGetWeeklyDuration() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRoomRepository.findByMemberId(memberId)).thenReturn(List.of(memberRoomEntity, memberRoomEntity2));

        //when
        List<StudyHistoryResponse> result = studyHistoryService.getDailyDuration(new StudyHistoryRequest(now.minusHours(1), now.plusDays(2)));

        //then
        assertThat(result.size()).isEqualTo(2);
    }
}