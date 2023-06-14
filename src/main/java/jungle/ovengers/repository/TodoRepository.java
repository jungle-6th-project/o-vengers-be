package jungle.ovengers.repository;

import jungle.ovengers.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
    List<TodoEntity> findByGroupIdAndMemberIdAndDeletedFalse(Long groupId, Long memberId);
}
