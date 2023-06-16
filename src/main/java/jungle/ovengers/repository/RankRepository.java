package jungle.ovengers.repository;

import jungle.ovengers.entity.RankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankRepository extends JpaRepository<RankEntity, Long> {
    List<RankEntity> findByGroupIdAndDeletedFalse(Long groupId);

    Optional<RankEntity> findByGroupIdAndMemberIdAndDeletedFalse(Long groupId, Long memberId);
}
