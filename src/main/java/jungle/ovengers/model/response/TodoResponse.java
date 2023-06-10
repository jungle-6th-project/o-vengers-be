package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodoResponse {
    private Long todoId;
    private Long groupId;
    private String group;
    private String content;
}
