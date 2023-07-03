package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.enums.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmailAndDeletedFalse(String email);

    Optional<MemberEntity> findByIdAndDeletedFalse(Long memberId);

    Optional<MemberEntity> findByCertificationIdAndDeletedFalse(Long certificationId);

    List<MemberEntity> findAllByStatus(MemberStatus status);
}
