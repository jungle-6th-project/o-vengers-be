package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {
    private String name;
    private String profile;
    private String email;
    private Duration duration;
}
