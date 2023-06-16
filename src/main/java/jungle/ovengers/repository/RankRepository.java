package jungle.ovengers.repository;

import jungle.ovengers.entity.RankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankRepository extends JpaRepository<RankEntity, Long> {
    List<RankEntity> findByGroupIdAndDeletedFalse(Long groupId);
}
