package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberGroupRepository extends JpaRepository<MemberGroupEntity, Long> {
    List<MemberGroupEntity> findByMemberId(Long memberId);
}
