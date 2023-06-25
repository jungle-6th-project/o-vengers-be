package jungle.ovengers.support.converter;

import jungle.ovengers.model.response.StudyHistoryResponse;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDate;

@UtilityClass
public final class StudyHistoryConverter {

    public static StudyHistoryResponse from(LocalDate calculatedAt, Duration duration) {
        return StudyHistoryResponse.builder()
                                   .calculatedAt(calculatedAt)
                                   .duration(duration.toString())
                                   .build();
    }
}
