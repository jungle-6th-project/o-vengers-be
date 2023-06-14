package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoomRepository extends JpaRepository<MemberRoomEntity, Long> {
    Optional<MemberRoomEntity> findByMemberIdAndRoomIdAndDeletedFalse(Long memberId, Long roomId);
}
