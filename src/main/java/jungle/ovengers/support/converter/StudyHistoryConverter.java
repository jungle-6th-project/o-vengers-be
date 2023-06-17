package jungle.ovengers.support.converter;

import jungle.ovengers.model.response.StudyHistoryResponse;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;

@UtilityClass
public final class StudyHistoryConverter {

    public static StudyHistoryResponse from(LocalDateTime createdAt, Duration duration) {
        return StudyHistoryResponse.builder()
                                   .createdAt(createdAt)
                                   .duration(duration)
                                   .build();
    }
}
