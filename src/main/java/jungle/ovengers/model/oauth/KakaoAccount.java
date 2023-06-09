package jungle.ovengers.model.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoAccount {
    private String email;
    private KakaoProfile profile;
    private String name;
}
