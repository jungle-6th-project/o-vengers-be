package jungle.ovengers.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupEditRequest {
    private Long groupId;
    private String groupName;
    private boolean isSecret;
    private String password;
}
