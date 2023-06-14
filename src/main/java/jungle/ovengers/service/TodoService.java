package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.TodoEntity;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.exception.TodoNotFoundException;
import jungle.ovengers.model.request.TodoAddRequest;
import jungle.ovengers.model.request.TodoEditRequest;
import jungle.ovengers.model.request.TodoReadRequest;
import jungle.ovengers.model.response.TodoResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.TodoRepository;
import jungle.ovengers.support.converter.TodoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final AuditorHolder auditorHolder;

    public TodoResponse generateTodo(TodoAddRequest request) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));
        groupRepository.findById(request.getGroupId())
                       .orElseThrow(() -> new GroupNotFoundException(request.getGroupId()));
        return TodoConverter.from(todoRepository.save(TodoConverter.to(memberId, request)));
    }

    public List<TodoResponse> getGroupTodos(TodoReadRequest request) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        return todoRepository.findByGroupIdAndMemberIdAndDeletedFalse(request.getGroupId(), memberId)
                             .stream()
                             .map(TodoConverter::from)
                             .collect(Collectors.toList());
    }

    public TodoResponse changeTodoInfo(TodoEditRequest request) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));

        TodoEntity todoEntity = todoRepository.findById(request.getTodoId())
                                              .orElseThrow(() -> new TodoNotFoundException(request.getTodoId()));
        todoEntity.changeTodoInfo(request);
        return TodoConverter.from(todoEntity);
    }
}
