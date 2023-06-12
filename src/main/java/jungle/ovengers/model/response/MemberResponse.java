package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private String name;
    private String profile;
    private String email;
    private Duration duration;
}
