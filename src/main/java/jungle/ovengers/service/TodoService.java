package jungle.ovengers.service;

import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.exception.GroupNotFoundException;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.model.request.TodoRequest;
import jungle.ovengers.model.response.TodoResponse;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.TodoRepository;
import jungle.ovengers.support.converter.TodoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final AuditorHolder auditorHolder;

    public TodoResponse generateTodo(Long groupId, TodoRequest request) {
        Long memberId = auditorHolder.get();

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));
        groupRepository.findById(groupId)
                       .orElseThrow(() -> new GroupNotFoundException(groupId));
        return TodoConverter.from(todoRepository.save(TodoConverter.to(memberId, groupId, request)));
    }
}
