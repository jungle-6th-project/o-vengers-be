package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.model.request.StudyHistoryRequest;
import jungle.ovengers.model.response.StudyHistoryResponse;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.support.converter.StudyHistoryConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StudyHistoryService {
    private final MemberRoomRepository memberRoomRepository;
    private final AuditorHolder auditorHolder;

    public StudyHistoryResponse getDailyDuration(StudyHistoryRequest request) {
        return StudyHistoryConverter.from(request.getFrom(), calculateDailyDuration(request));
    }

    private Duration calculateDailyDuration(StudyHistoryRequest request) {
        Long memberId = auditorHolder.get();
        List<MemberRoomEntity> memberRoomEntities = memberRoomRepository.findByMemberId(memberId)
                                                             .stream()
                                                             .filter(memberRoomEntity -> memberRoomEntity.isAfter(request.getFrom()) && memberRoomEntity.isBefore(request.getTo()))
                                                             .collect(Collectors.toList());

        return memberRoomEntities.stream()
                                 .map(MemberRoomEntity::getDurationTime)
                                 .reduce(Duration.ZERO, Duration::plus);
    }
}
