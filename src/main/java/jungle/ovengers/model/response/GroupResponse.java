package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponse {
    private Long groupId;
    private String groupName;
    private boolean isSecret;
    private String color;
    private String path;
}
