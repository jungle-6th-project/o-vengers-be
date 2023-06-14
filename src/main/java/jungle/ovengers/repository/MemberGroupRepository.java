package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberGroupRepository extends JpaRepository<MemberGroupEntity, Long> {
    List<MemberGroupEntity> findByMemberId(Long memberId);

    List<MemberGroupEntity> findByMemberIdAndDeletedFalse(Long memberId);

    List<MemberGroupEntity> findByGroupId(Long groupId);

    boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);

    Optional<MemberGroupEntity> findByGroupIdAndMemberId(Long groupId, Long memberId);
    Optional<MemberGroupEntity> findByGroupIdAndMemberIdAndDeletedFalse(Long groupId, Long memberId);

}
