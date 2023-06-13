package jungle.ovengers.support.converter;

import jungle.ovengers.entity.TodoEntity;
import jungle.ovengers.model.request.TodoAddRequest;
import jungle.ovengers.model.response.TodoResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class TodoConverter {

    public TodoEntity to(Long memberId, TodoAddRequest request) {
        return TodoEntity.builder()
                         .memberId(memberId)
                         .groupId(request.getGroupId())
                         .content(request.getContent())
                         .done(false)
                         .createdTime(LocalDateTime.now())
                         .updatedTime(LocalDateTime.now())
                         .build();
    }

    public TodoResponse from(TodoEntity todoEntity) {
        return TodoResponse.builder()
                           .todoId(todoEntity.getId())
                           .groupId(todoEntity.getGroupId())
                           .content(todoEntity.getContent())
                           .build();
    }
}
