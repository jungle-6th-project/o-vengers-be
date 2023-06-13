package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.TodoEntity;
import jungle.ovengers.model.request.TodoRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    private Long memberId;
    private Long groupId;
    private GroupEntity groupEntity;
    private MemberEntity memberEntity;
    private TodoEntity todoEntity;

    @BeforeEach
    public void setup() {
        memberId = 1L;
        groupId = 1L;
        groupEntity = GroupEntity.builder()
                                 .id(groupId)
                                 .ownerId(memberId)
                                 .path("path")
                                 .groupName("groupName")
                                 .isSecret(false)
                                 .createdAt(LocalDateTime.now())
                                 .deleted(false)
                                 .build();
        memberEntity = MemberEntity.builder()
                                   .id(memberId)
                                   .email("email")
                                   .profile("profile")
                                   .name("name")
                                   .deleted(false)
                                   .build();
        todoEntity = TodoEntity.builder()
                               .id(1L)
                               .memberId(memberId)
                               .groupId(groupId)
                               .content("content")
                               .done(false)
                               .createdTime(LocalDateTime.now())
                               .updatedTime(LocalDateTime.now())
                               .build();
    }

    @DisplayName("사용자가 해당하는 그룹에 Todo를 생성했을때, 잘 추가되는지 테스트")
    @Test
    public void testGenerateTodo() {
        //given
        when(auditorHolder.get()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(groupEntity));
        when(todoRepository.save(any(TodoEntity.class))).thenReturn(todoEntity);
        //when
        TodoResponse result = todoService.generateTodo(groupId, new TodoRequest("content"));
        //then
        assertThat(result.getTodoId()).isEqualTo(todoEntity.getId());
        assertThat(result.getContent()).isEqualTo(todoEntity.getContent());
        assertThat(result.getGroupId()).isEqualTo(todoEntity.getGroupId());
    }
}