package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientAddResponse {
    private Long id;
    private Long memberId;
    private String fcmToken;
}
