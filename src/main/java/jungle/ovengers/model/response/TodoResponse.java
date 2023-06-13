package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodoResponse {
    private Long todoId;
    private Long groupId;
    private String content;
}
