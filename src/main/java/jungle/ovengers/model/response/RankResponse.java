package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankResponse {
    private Long memberId;
    private String profile;
    private String nickname;
    private Duration duration;
}
