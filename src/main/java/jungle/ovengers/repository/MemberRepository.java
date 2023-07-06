package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmailAndDeletedFalse(String email);

    Optional<MemberEntity> findByIdAndDeletedFalse(Long memberId);

    Optional<MemberEntity> findByCertificationIdAndDeletedFalse(Long certificationId);

    List<MemberEntity> findAllByStatus(MemberStatus status);

    List<MemberEntity> findAllByStatus(MemberStatus status, Pageable pageable);

    @Query("SELECT m FROM MemberEntity m WHERE m.id IN ?1 AND m.status = ?2")
    List<MemberEntity> findAllByMemberIdsAndStatus(List<Long> memberIds, MemberStatus status);
}
