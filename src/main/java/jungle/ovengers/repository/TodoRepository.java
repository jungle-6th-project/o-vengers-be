package jungle.ovengers.repository;

import jungle.ovengers.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    List<TodoEntity> findByGroupIdAndMemberIdAndDeletedFalse(Long groupId, Long memberId);

    List<TodoEntity> findByGroupIdAndDeletedFalse(Long groupId);

    List<TodoEntity> findByDoneTrue();

    Optional<TodoEntity> findByIdAndDeletedFalse(Long todoId);
}
