package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmailAndDeletedFalse(String email);

    Optional<MemberEntity> findByIdAndDeletedFalse(Long memberId);
}
