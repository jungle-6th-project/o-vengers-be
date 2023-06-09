package jungle.ovengers.repository;

import jungle.ovengers.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    Optional<GroupEntity> findByIdAndDeletedFalse(Long groupId);

    Optional<GroupEntity> findByPathAndDeletedFalse(String path);

    List<GroupEntity> findAllByDeletedFalse();
}
