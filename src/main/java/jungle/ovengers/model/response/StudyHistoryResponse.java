package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudyHistoryResponse {
    private Date create_at;
    private Duration duration;
}
