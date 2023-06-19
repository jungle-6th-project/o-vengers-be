package jungle.ovengers.repository;

import jungle.ovengers.entity.RoomEntity;
import jungle.ovengers.entity.RoomEntryHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomEntryHistoryRepository extends JpaRepository<RoomEntryHistoryEntity, Long> {
    Optional<RoomEntryHistoryEntity> findByMemberRoomIdAndExitTimeIsNull(Long memberRoomId);
}
