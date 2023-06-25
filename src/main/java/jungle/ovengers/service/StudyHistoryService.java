package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.MemberRoomEntity;
import jungle.ovengers.model.request.StudyHistoryRequest;
import jungle.ovengers.model.response.StudyHistoryResponse;
import jungle.ovengers.repository.MemberRoomRepository;
import jungle.ovengers.support.converter.StudyHistoryConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class StudyHistoryService {
    private final MemberRoomRepository memberRoomRepository;
    private final AuditorHolder auditorHolder;


    public String makeRedisKey() {
        return "history" + auditorHolder.get()
                                        .toString();
    }

//    @Cacheable(cacheNames = "dailyDuration", key = "{#root.target.makeRedisKey()}")
    public List<StudyHistoryResponse> getDailyDuration(StudyHistoryRequest request) {
        log.info("Cache Study History");
        Map<LocalDate, Duration> localDateDurationMap = calculateDailyDuration(request);
        return localDateDurationMap.keySet()
                                   .stream()
                                   .map(localDate -> StudyHistoryConverter.from(localDate, localDateDurationMap.get(localDate)))
                                   .collect(Collectors.toList());
    }

    private Map<LocalDate, Duration> calculateDailyDuration(StudyHistoryRequest request) {
        Long memberId = auditorHolder.get();
        List<MemberRoomEntity> memberRoomEntities = memberRoomRepository.findByMemberId(memberId)
                                                                        .stream()
                                                                        .filter(memberRoomEntity -> memberRoomEntity.isAfter(request.getFrom()) && memberRoomEntity.isBefore(request.getTo()))
                                                                        .collect(Collectors.toList());

        return memberRoomEntities.stream()
                                 .collect(Collectors.groupingBy(memberRoomEntity -> memberRoomEntity.getStartTime()
                                                                                                    .toLocalDate(),
                                                                Collectors.mapping(MemberRoomEntity::getDurationTime, Collectors.reducing(Duration.ZERO, Duration::plus))));
    }
}
