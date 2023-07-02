package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.data.FakeGroupInitializer;
import jungle.ovengers.data.FakeMemberInitializer;
import jungle.ovengers.data.FakeTodoInitializer;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.TodoEntity;
import jungle.ovengers.model.request.TodoAddRequest;
import jungle.ovengers.model.request.TodoDeleteRequest;
import jungle.ovengers.model.request.TodoEditRequest;
import jungle.ovengers.model.request.TodoReadRequest;
import jungle.ovengers.model.response.TodoResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private AuditorHolder auditorHolder;
    @InjectMocks
    private TodoService todoService;
    private GroupEntity groupEntity;
    private MemberEntity memberEntity;
    private TodoEntity todoEntity;

    @BeforeEach
    public void setup() {
        groupEntity = FakeGroupInitializer.of();
        memberEntity = FakeMemberInitializer.of();
        todoEntity = FakeTodoInitializer.of();
    }

    @DisplayName("사용자가 해당하는 그룹에 Todo를 생성했을때, 잘 추가되는지 테스트")
    @Test
    public void testGenerateTodo() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(todoEntity);
        //when
        TodoResponse result = todoService.generateTodo(new TodoAddRequest("content", groupId));
        //then
        assertThat(result.getTodoId()).isEqualTo(todoEntity.getId());
        assertThat(result.getContent()).isEqualTo(todoEntity.getContent());
        assertThat(result.getGroupId()).isEqualTo(todoEntity.getGroupId());
        assertThat(result.isDone()).isFalse();
    }

    @DisplayName("사용자가 속해있는 그룹의 todo를 조회하는 요청이 왔을때, 잘 조회되는지 테스트")
    @Test
    public void testGetGroupTodos() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();

        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(memberEntity));
        when(todoRepository.findByGroupIdAndMemberIdAndDeletedFalse(groupId, memberId)).thenReturn(Collections.singletonList(todoEntity));
        //when
        List<TodoResponse> result = todoService.getGroupTodos(new TodoReadRequest(groupId));
        //then
        assertThat(result.get(0)
                         .getGroupId()).isEqualTo(todoEntity.getGroupId());
        assertThat(result.get(0)
                         .getTodoId()).isEqualTo(todoEntity.getId());
        assertThat(result.get(0)
                         .getContent()).isEqualTo(todoEntity.getContent());
    }

    @DisplayName("Todo 내용 수정이 잘 되는지 테스트")
    @Test
    public void testEditTodoContent() {
        //given
        Long todoId = todoEntity.getId();

        when(todoRepository.findByIdAndDeletedFalse(todoId)).thenReturn(Optional.of(todoEntity));
        when(memberRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(memberEntity));
        //when
        TodoResponse result = todoService.changeTodoInfo(new TodoEditRequest(todoId, "changedContent", todoEntity.isDone()));
        //then
        assertThat(result.isDone()).isEqualTo(todoEntity.isDone());
        assertThat(result.getContent()).isEqualTo("changedContent");
    }

    @DisplayName("Todo 완료 여부가 false에서 true로 잘 변경 되는지 테스트")
    @Test
    public void testEditTodoDoneFalseToTrue() {
        //given
        Long todoId = todoEntity.getId();

        when(memberRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(memberEntity));
        when(todoRepository.findByIdAndDeletedFalse(todoId)).thenReturn(Optional.of(todoEntity));
        //when
        TodoResponse result = todoService.changeTodoInfo(new TodoEditRequest(todoId, todoEntity.getContent(), true));
        //then
        assertThat(result.isDone()).isTrue();
    }

    @DisplayName("Todo 완료 여부가 true에서 false로 잘 변경 되는지 테스트")
    @Test
    public void testEditTodoDoneTrueToFalse() {
        //given
        Long memberId = memberEntity.getId();
        Long groupId = groupEntity.getId();
        Long todoId = todoEntity.getId();

        TodoEntity todoEntity = TodoEntity.builder()
                                          .id(todoId)
                                          .memberId(memberId)
                                          .groupId(groupId)
                                          .content("content")
                                          .done(true)
                                          .createdTime(LocalDateTime.now())
                                          .doneAt(LocalDateTime.now())
                                          .deleted(false)
                                          .build();
        when(memberRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(memberEntity));
        when(todoRepository.findByIdAndDeletedFalse(todoId)).thenReturn(Optional.of(todoEntity));
        //when
        TodoResponse result = todoService.changeTodoInfo(new TodoEditRequest(todoId, todoEntity.getContent(), false));
        //then
        assertThat(result.isDone()).isFalse();
    }

    @DisplayName("Todo 삭제 요청시 삭제가 잘 되는지 테스트")
    @Test
    public void testDeleteTodo() {
        //given
        Long todoId = todoEntity.getId();

        when(memberRepository.findByIdAndDeletedFalse(any())).thenReturn(Optional.of(memberEntity));
        //when
        todoService.deleteTodo(new TodoDeleteRequest(todoId));
        //then
        verify(todoRepository, times(1)).findByIdAndDeletedFalse(todoId);
    }
}