package jungle.ovengers.data;

import jungle.ovengers.entity.TodoEntity;

import java.time.LocalDateTime;

public final class FakeTodoInitializer {

    public static TodoEntity of() {
        return TodoEntity.builder()
                         .id(1L)
                         .groupId(1L)
                         .content("content")
                         .done(false)
                         .deleted(false)
                         .createdAt(LocalDateTime.now())
                         .memberId(1L)
                         .build();
    }

    public static TodoEntity of(Long todoId, Long groupId, Long memberId) {
        return TodoEntity.builder()
                         .id(todoId)
                         .groupId(groupId)
                         .memberId(memberId)
                         .content("content")
                         .done(false)
                         .deleted(false)
                         .createdAt(LocalDateTime.now())
                         .build();
    }
}
