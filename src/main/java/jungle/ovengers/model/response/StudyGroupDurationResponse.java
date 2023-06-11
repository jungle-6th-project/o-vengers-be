package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupDurationResponse {
    private Long groupId;
    private Long groupName;
    private Duration group_duration;
}
