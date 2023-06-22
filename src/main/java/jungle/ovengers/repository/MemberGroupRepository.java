package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberGroupRepository extends JpaRepository<MemberGroupEntity, Long> {
    List<MemberGroupEntity> findByMemberIdAndDeletedFalse(Long memberId);

    boolean existsByGroupIdAndMemberIdAndDeletedFalse(Long groupId, Long memberId);

    boolean existsByGroupIdAndDeletedFalse(Long groupId);

    Optional<MemberGroupEntity> findByGroupIdAndMemberIdAndDeletedFalse(Long groupId, Long memberId);

}
