package jungle.ovengers.model.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserInfoResponse {
    private long id;
    private boolean hasSignedUp;
    private LocalDateTime connectedAt;
    private KakaoAccount kakaoAccount;
}
