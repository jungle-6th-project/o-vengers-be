package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByEmail(String email);
}
