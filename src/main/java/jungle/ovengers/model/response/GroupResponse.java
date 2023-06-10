package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private Long groupId;
    private String groupName;
    private boolean isSecret;
}
