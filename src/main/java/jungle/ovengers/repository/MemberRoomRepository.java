package jungle.ovengers.repository;

import jungle.ovengers.entity.MemberRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRoomRepository extends JpaRepository<MemberRoomEntity, Long> {
    Optional<MemberRoomEntity> findByMemberIdAndRoomIdAndDeletedFalse(Long memberId, Long roomId);

    List<MemberRoomEntity> findByRoomIdAndDeletedFalse(Long roomId);
    boolean existsByRoomIdAndDeletedFalse(Long roomId);

    boolean existsByMemberIdAndRoomIdAndDeletedFalse(Long memberId, Long roomId);

    List<MemberRoomEntity> findByMemberId(Long memberId);
}
